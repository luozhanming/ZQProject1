package cn.com.ava.zqproject.ui.videoResource.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.usb.DownloadObject
import cn.com.ava.zqproject.usb.UsbHelper
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.concurrent.thread

class DownloadService : Service() {

    private var mCallback: DownloadCallback? = null
    private var downloadInfo: ConcurrentMap<String, RecordFilesInfo.RecordFile> = ConcurrentHashMap<String, RecordFilesInfo.RecordFile>()

    private val mBinder by lazy {
        DownloadBinder(this)
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

    // 注册下载回调
    fun registerDownloadCallback(callback: DownloadCallback) {
        mCallback = callback
    }

    interface DownloadCallback {
        fun onDownloadStateChanged(info: ConcurrentMap<String, RecordFilesInfo.RecordFile>)
    }
}