package cn.com.ava.zqproject.ui.videoResource.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.entity.TransmissionProgressEntity
import cn.com.ava.lubosdk.manager.VideoResourceManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.net.PlatformApiManager
import cn.com.ava.zqproject.ui.videoResource.util.AESUtils
import cn.com.ava.zqproject.usb.DownloadObject
import cn.com.ava.zqproject.usb.UsbHelper
import com.blankj.utilcode.util.ToastUtils
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

    var uploadFileNames = mutableListOf<String>()

    fun getUploadVideoProgress(fileName: String)  {
        mDisposables.add(VideoResourceManager.checkUploadProgress(fileName)
            .compose(PlatformApi.applySchedulers())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
//                logd("uploadVideo currentThread ${Thread.currentThread()}")
                uploadInfo.put(fileName, it)
                try {
                    mCallback?.onUploadStateChanged(uploadInfo)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (it.state == 1 || it.state == 3) { // 上传成功或者失败就移出队列
                    uploadFileNames.remove(fileName)
                    uploadInfo.remove(fileName)
                }
            }
        )
    }

    override fun onCreate() {
        super.onCreate()
        mDisposables.add(
            Flowable.interval(2000, TimeUnit.MILLISECONDS).doOnNext {
//                logd("上传视频onCreate")
                uploadFileNames.forEach {
                    if (uploadInfo[it]?.state != 1) {
                        getUploadVideoProgress(it)
                    }
                }
            }.subscribeOn(Schedulers.io()).subscribe({

            }, {

            })
        )
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

    /*
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
                        downloadInfo.remove(file?.obj?.downloadUrl)
                    }
                }
            })
        }
    }

    /*
    * 上传视频
    * */
    fun uploadVideo(data: RecordFilesInfo.RecordFile, desc: String) {
        val ftpInfo = PlatformApiManager.getApiPath(PlatformApiManager.PATH_FTP_INFO)
        logd("ftpInfo = $ftpInfo")
        // AES解密
        val decryptStr = AESUtils.decrypt(ftpInfo, AESUtils.PASSWORD_CRYPT_KEY)
        logd("decrypt ftpInfo = $decryptStr")

        mDisposables.add(
            VideoResourceManager.uploadRecordFile2(json2Map(decryptStr) ?: HashMap(), data)
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (result == true) {
                        logd("提交上传视频资源接口成功")
                        mCallback?.onSubmitUploadCallback(true, "正在上传" + data.downloadFileName, data)

                        if (uploadFileNames?.contains(data.rawFileName) == false) {
                            uploadFileNames.add(0, data.rawFileName)
                        }
                    } else {
                        mCallback?.onSubmitUploadCallback(false, "上传失败，请重试", data)
//                        ToastUtils.showShort("上传失败，请重试")
//                        logd("上传视频资源提交失败")
                    }
                }, {
                    logd("提交上传视频资源接口出错")
                    logPrint2File(it)
                    mCallback?.onSubmitUploadCallback(false, "上传失败，请重试", data)
                })
        )
    }

    /*
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

    interface DownloadCallback {
        fun onDownloadStateChanged(info: ConcurrentMap<String, RecordFilesInfo.RecordFile>)
        fun onSubmitUploadCallback(isSuccess: Boolean, msg: String, data: RecordFilesInfo.RecordFile)
        fun onUploadStateChanged(info: ConcurrentMap<String, TransmissionProgressEntity>)
    }
}