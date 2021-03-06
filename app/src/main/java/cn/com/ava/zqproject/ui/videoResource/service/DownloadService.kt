package cn.com.ava.zqproject.ui.videoResource.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.GsonUtil
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.entity.TransmissionProgressEntity
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.manager.VideoResourceManager
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.net.PlatformApiManager
import cn.com.ava.zqproject.ui.videoResource.VideoPreference
import cn.com.ava.zqproject.ui.videoResource.util.AESUtils
import cn.com.ava.zqproject.usb.DownloadObject
import cn.com.ava.zqproject.usb.UsbHelper
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class DownloadService : Service() {

    private var mCallback: DownloadCallback? = null
    private var downloadInfo: ConcurrentMap<String, RecordFilesInfo.RecordFile> = ConcurrentHashMap<String, RecordFilesInfo.RecordFile>()
    private var uploadInfo: ConcurrentMap<String, TransmissionProgressEntity> = ConcurrentHashMap<String, TransmissionProgressEntity>()

    protected val mDisposables: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private val mBinder by lazy {
        DownloadBinder(this)
    }

    var uploadVideos = mutableListOf<RecordFilesInfo.RecordFile>()

    fun getUploadVideoProgress(data: RecordFilesInfo.RecordFile)  {
        mDisposables.add(VideoResourceManager.checkUploadProgress(data.rawFileName)
            .compose(PlatformApi.applySchedulers())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                uploadInfo.put(data.rawFileName, it)
                try {
                    // ????????????????????????
                    VideoSingleton.cacheVideos.forEach {
                        if (uploadInfo.containsKey(it.rawFileName) && it.transmissionType == 2) {
                            it.uploadState = uploadInfo.get(it.rawFileName)!!.state
                            it.uploadProgress = uploadInfo.get(it.rawFileName)!!.progress
                            logd("???????????????${it.uploadProgress}")
                            if (it.uploadState == 1) { // ????????????
                                ToastUtils.showShort(it.downloadFileName + getResources().getString(
                                    R.string.tip_upload_success))
                            } else if (it.uploadState == 3) { // ????????????
                                ToastUtils.showShort(it.downloadFileName + getResources().getString(
                                    R.string.tip_upload_failed))
                            }
                        }
                    }
                    mCallback?.onUploadStateChanged(uploadInfo)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (it.state == 1 || it.state == 3) { // ???????????????????????????????????????
                    saveCache()
                    if (it.state == 1) { // ????????????
                        saveVideoFromFTP(data)
                    }
                    uploadVideos.remove(data)
                    uploadInfo.remove(data.rawFileName)
                }
            }
        )
    }

    override fun onCreate() {
        super.onCreate()
        mDisposables.add(
            Flowable.interval(2000, TimeUnit.MILLISECONDS).doOnNext {
//                logd("????????????onCreate")
                saveCache()
                uploadVideos.forEach {
                    if (uploadInfo[it.rawFileName]?.state != 1) {
                        getUploadVideoProgress(it)
                    }
                }
            }.subscribeOn(Schedulers.io()).subscribe({

            }, {

            })
        )
    }

    fun saveCache() {
        if (downloadInfo.keys.size == 0 && uploadInfo.keys.size == 0) return
        logd("???????????????")
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(VideoSingleton.cacheVideos))
    }

    class DownloadBinder(service: DownloadService) : Binder() {
        private val mService: WeakReference<DownloadService>?
        val service : DownloadService?
            get() = mService?.get()

        fun release() {
            mService?.clear()
        }

        init {
            mService = WeakReference(service)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinder.release()
    }

    /**
    * ????????????
    * */
    fun downloadVideo(data: RecordFilesInfo.RecordFile) {

        thread {
            logd("????????????????????????${Thread.currentThread().toString()}")
            UsbHelper.getHelper().downloadFile2UDisk(data, data.getDownloadFileName())

            UsbHelper.getHelper().registerDownloadCallback(object : UsbHelper.FileDownloadCallback {
                override fun onDownloadStateChanged(
                    file: DownloadObject<RecordFilesInfo.RecordFile>?,
                    dstPath: String?,
                    state: Int,
                    progress: Int
                ) {
                    logd("???????????????${file?.obj?.downloadFileName}, ??????????????? $dstPath, ??????????????? $progress, ??????: $state")
                    var video = file?.obj
                    video?.let {
                        video.downloadDstPath = dstPath
                        video.downloadProgress = progress
                    }
                    downloadInfo.put(file?.obj?.downloadUrl, video)
                    mCallback?.onDownloadStateChanged(downloadInfo)
                    if (state == 1003) { // ????????????????????????
                        saveCache()
                        downloadInfo.remove(file?.obj?.downloadUrl)
                    }
                }
            })
        }
    }

    /**
    * ????????????
    * */
    fun uploadVideo(data: RecordFilesInfo.RecordFile, desc: String) {
        val video = RecordFilesInfo.RecordFile(data)
        video.transmissionType = 2

        val ftpInfo = PlatformApiManager.getApiPath(PlatformApiManager.PATH_FTP_INFO)
        logd("ftpInfo = $ftpInfo")
        // AES??????
        val decryptStr = AESUtils.decrypt(ftpInfo, AESUtils.PASSWORD_CRYPT_KEY)
        logd("decrypt ftpInfo = $decryptStr")

        val uuid = "0_${PlatformApi.getPlatformLogin()?.id}_${System.currentTimeMillis()}"
        val dstFile = uuid + "${video.recordRawBeginTime}.mp4"
//        val dstFile = "1.mp4"
        video.uploadUUID = uuid
        logd("uuid = $uuid, recid = ${video.recordRawBeginTime}, dstFile = $dstFile")
        mDisposables.add(
            VideoResourceManager.uploadRecordFile2(json2Map(decryptStr) ?: HashMap(), video, dstFile)
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->

                    if (result == true) {
                        logd("????????????????????????????????????")
                        // ??????????????????????????????
                        VideoSingleton.cacheVideos.addAll(0, arrayListOf(video))
                        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(VideoSingleton.cacheVideos))

                        mCallback?.onSubmitUploadCallback(true, "????????????" + video.downloadFileName, video)

                        if (uploadVideos?.contains(video) == false) {
                            uploadVideos.add(0, video)
                        }
                    } else {
                        mCallback?.onSubmitUploadCallback(false, "????????????????????????", video)
                    }
                }, {
                    logd("????????????????????????????????????")
                    logPrint2File(it,"DownloadService#uploadVideo")
                    mCallback?.onSubmitUploadCallback(false, "????????????????????????", video)
                })
        )
    }

    /**
     * ??????FTP??????????????????
     * */
    fun saveVideoFromFTP(data: RecordFilesInfo.RecordFile) {
        val uuid = data.uploadUUID
        val fileName = data.recordRawBeginTime + ".mp4"
        val title = data.downloadFileName
        val period = data.rawDuration.toInt()
        logd("fileName = $fileName, period = $period, uuid = $uuid")
        mDisposables.add(
            PlatformApi.getService()
                .saveVideoFromFTP(fileName = fileName, title = title, period = period, uuid = uuid)
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    logd("??????FTP?????????????????? success")
                }, {
                    logd("??????FTP?????????????????? error ${it.message}")
                    logPrint2File(it,"DownloadService#saveVideoFromFTP")
                })
        )

    }

    /**
    * json???map
    * */
    fun json2Map(jsonString: String?): HashMap<String, Any>? {
        val jsonObject: JSONObject
        try {
            jsonObject = JSONObject(jsonString)
            val keyIter: Iterator<String> = jsonObject.keys()
            var key: String
            var value: Any
            var valueMap = HashMap<String, Any>()
            while (keyIter.hasNext()) {
                key = keyIter.next()
                value = jsonObject[key] as Any
                valueMap[key] = value
            }
            return valueMap
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    // ??????????????????
    fun registerDownloadCallback(callback: DownloadCallback) {
        mCallback = callback
    }

    // ??????????????????
    fun unRegisterDownloadCallback() {
        mCallback = null
    }

    interface DownloadCallback {
        fun onDownloadStateChanged(info: ConcurrentMap<String, RecordFilesInfo.RecordFile>)
        fun onSubmitUploadCallback(isSuccess: Boolean, msg: String, data: RecordFilesInfo.RecordFile)
        fun onUploadStateChanged(info: ConcurrentMap<String, TransmissionProgressEntity>)
    }
}