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
                    // 更新本地缓存进度
                    VideoSingleton.cacheVideos.forEach {
                        if (uploadInfo.containsKey(it.rawFileName) && it.transmissionType == 2) {
                            it.uploadState = uploadInfo.get(it.rawFileName)!!.state
                            it.uploadProgress = uploadInfo.get(it.rawFileName)!!.progress
                            logd("上传进度：${it.uploadProgress}")
                            if (it.uploadState == 1) { // 上传成功
                                ToastUtils.showShort(it.downloadFileName + getResources().getString(
                                    R.string.tip_upload_success))
                            } else if (it.uploadState == 3) { // 上传失败
                                ToastUtils.showShort(it.downloadFileName + getResources().getString(
                                    R.string.tip_upload_failed))
                            }
                        }
                    }
                    mCallback?.onUploadStateChanged(uploadInfo)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (it.state == 1 || it.state == 3) { // 上传成功或者失败就移出队列
                    saveCache()
                    if (it.state == 1) { // 上传成功
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
//                logd("上传视频onCreate")
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
        logd("缓存在本地")
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
    * 下载视频
    * */
    fun downloadVideo(data: RecordFilesInfo.RecordFile) {

        thread {
            logd("开始下载的线程：${Thread.currentThread().toString()}")
            UsbHelper.getHelper().downloadFile2UDisk(data, data.getDownloadFileName())

            UsbHelper.getHelper().registerDownloadCallback(object : UsbHelper.FileDownloadCallback {
                override fun onDownloadStateChanged(
                    file: DownloadObject<RecordFilesInfo.RecordFile>?,
                    dstPath: String?,
                    state: Int,
                    progress: Int
                ) {
                    logd("视频名称：${file?.obj?.downloadFileName}, 目的路径： $dstPath, 下载进度： $progress, 状态: $state")
                    var video = file?.obj
                    video?.let {
                        video.downloadDstPath = dstPath
                        video.downloadProgress = progress
                    }
                    downloadInfo.put(file?.obj?.downloadUrl, video)
                    mCallback?.onDownloadStateChanged(downloadInfo)
                    if (state == 1003) { // 下载完成需要移除
                        saveCache()
                        downloadInfo.remove(file?.obj?.downloadUrl)
                    }
                }
            })
        }
    }

    /**
    * 上传视频
    * */
    fun uploadVideo(data: RecordFilesInfo.RecordFile, desc: String) {
        val video = RecordFilesInfo.RecordFile(data)
        video.transmissionType = 2

        val ftpInfo = PlatformApiManager.getApiPath(PlatformApiManager.PATH_FTP_INFO)
        logd("ftpInfo = $ftpInfo")
        // AES解密
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
                        logd("提交上传视频资源接口成功")
                        // 把上传记录保存在本地
                        VideoSingleton.cacheVideos.addAll(0, arrayListOf(video))
                        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(VideoSingleton.cacheVideos))

                        mCallback?.onSubmitUploadCallback(true, "正在上传" + video.downloadFileName, video)

                        if (uploadVideos?.contains(video) == false) {
                            uploadVideos.add(0, video)
                        }
                    } else {
                        mCallback?.onSubmitUploadCallback(false, "上传失败，请重试", video)
                    }
                }, {
                    logd("提交上传视频资源接口出错")
                    logPrint2File(it,"DownloadService#uploadVideo")
                    mCallback?.onSubmitUploadCallback(false, "上传失败，请重试", video)
                })
        )
    }

    /**
     * 保存FTP中的视频文件
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
                    logd("保存FTP中的视频文件 success")
                }, {
                    logd("保存FTP中的视频文件 error ${it.message}")
                    logPrint2File(it,"DownloadService#saveVideoFromFTP")
                })
        )

    }

    /**
    * json转map
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

    // 注册下载回调
    fun registerDownloadCallback(callback: DownloadCallback) {
        mCallback = callback
    }

    // 注册下载回调
    fun unRegisterDownloadCallback() {
        mCallback = null
    }

    interface DownloadCallback {
        fun onDownloadStateChanged(info: ConcurrentMap<String, RecordFilesInfo.RecordFile>)
        fun onSubmitUploadCallback(isSuccess: Boolean, msg: String, data: RecordFilesInfo.RecordFile)
        fun onUploadStateChanged(info: ConcurrentMap<String, TransmissionProgressEntity>)
    }
}