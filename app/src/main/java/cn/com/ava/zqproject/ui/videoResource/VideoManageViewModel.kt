package cn.com.ava.zqproject.ui.videoResource

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageVolume
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.GsonUtil
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.common.CanRefresh
import cn.com.ava.zqproject.usb.DownloadObject
import cn.com.ava.zqproject.usb.UsbHelper
import cn.com.ava.zqproject.vo.RefreshState
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ConcurrentMap

class VideoManageViewModel : BaseViewModel(), CanRefresh {

    override val refreshState: MutableLiveData<RefreshState> by lazy {
        MutableLiveData()
    }

    val videoResources: MutableLiveData<MutableList<RecordFilesInfo.RecordFile>> by lazy {
        val data = arrayListOf<cn.com.ava.lubosdk.entity.RecordFilesInfo.RecordFile>()
        val liveData = MutableLiveData<MutableList<RecordFilesInfo.RecordFile>>()
        liveData.value = data
        liveData
    }

    val transmissionVideos: MutableLiveData<MutableList<RecordFilesInfo.RecordFile>> by lazy {
        val data = MutableLiveData<MutableList<RecordFilesInfo.RecordFile>>()
        val list = arrayListOf<RecordFilesInfo.RecordFile>()
        data.value = list
        data
    }

    // 搜索关键词
    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // 搜索过滤后的视频
    val filterVideos: MutableLiveData<List<RecordFilesInfo.RecordFile>> by lazy {
        MediatorLiveData<List<RecordFilesInfo.RecordFile>>().apply {
            //value = arrayListOf()
            addSource(searchKey) { key ->
                // 关键词过滤规则
                val list = videoResources.value
                val filter = list?.filter {
                    return@filter (it.downloadFileName.contains(key))
                }
                postValue(filter)
            }
        }
    }

    // 根据文件大小排序
    fun sortByFileSize() {
        // rawFileSize
        val list = videoResources.value
        // 指定以 rawFileSize 属性进行降序排序
        list?.sortByDescending { video -> video.rawFileSize }
        videoResources.value = list
    }

    // 根据录制时间排序
    fun sortByRecordTime() {
        // rawDuration
        val list = videoResources.value
        // 指定以 rawDuration 属性进行降序排序
        list?.sortByDescending { video -> video.recordRawBeginTime }
        videoResources.value = list
    }
    // 下载视频
//    fun downloadVideo(video: RecordFilesInfo.RecordFile) {
//        // 检测是否已经有下载缓存
//        if (checkCacheResult(video)) {
//            return
//        }
//
//        var cacheList = getCacheVideos()
//
//        cacheList.add(0, video)
//        transmissionVideos.value = cacheList
//        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(cacheList))
//
//        UsbHelper.getHelper().downloadFile2UDisk(video, video.getDownloadFileName())
//        UsbHelper.getHelper().registerDownloadCallback(object : UsbHelper.FileDownloadCallback {
//            override fun onDownloadStateChanged(
//                file: DownloadObject<RecordFilesInfo.RecordFile>?,
//                dstPath: String?,
//                state: Int,
//                progress: Int
//            ) {
//                logd("视频名称：${file?.obj?.downloadFileName}, 目的路径： $dstPath, 下载进度： $progress, 状态: $state")
//
//                var cacheList = getCacheVideos()
//                cacheList.forEach {
//                    if (it.downloadUrl == file?.obj?.downloadUrl) {
//                        it.downloadDstPath = dstPath
//                        it.downloadProgress = progress
//                    }
//                }
//                transmissionVideos.value = cacheList
//                VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(cacheList))
//            }
//        })
//    }

    // 检测是否有缓存
    fun checkCacheResult(data: RecordFilesInfo.RecordFile): Boolean {
        var cacheList = getCacheVideos()

        logd("本地视频: ${cacheList.toString()}")

        cacheList.forEach {
            if (data.downloadUrl == it.downloadUrl) {
                if (it.downloadProgress < 100) {
                    ToastUtils.showShort("该视频正在下载中")
                } else {
                    ToastUtils.showShort("该视频已下载完成")
                }
                return true
            }
        }
        return false
    }

    // 获取本地缓存的视频
    fun getCacheVideos(): ArrayList<RecordFilesInfo.RecordFile> {
        val cacheVideos = VideoPreference.getElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, "")
        var cacheList = arrayListOf<RecordFilesInfo.RecordFile>()
        if (cacheVideos != null && cacheVideos.length != 0) {
            cacheList = Gson().fromJson<ArrayList<RecordFilesInfo.RecordFile>>(cacheVideos, object : TypeToken<ArrayList<RecordFilesInfo.RecordFile>>(){}.type)
        }
//        logd("本地视频: ${cacheList.toString()}")
        return cacheList
    }

    // 获取录播视频
    fun getVideoResourceList() {
        mDisposables.add(
            GeneralManager.loadRecordFiles()
            .compose(PlatformApi.applySchedulers())
            .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                refreshState.postValue(RefreshState(true, false))
                videoResources.postValue(it.toMutableList())
                logd("视频资源请求成功")
            }, {
                logPrint2File(it)
                logd("视频资源请求失败")
                refreshState.postValue(RefreshState(true, true))
            })
        )
    }
    // 删除视频
    fun deleteVideo(video: RecordFilesInfo.RecordFile) {
        val videos = videoResources.value
        videos?.remove(video)
        videoResources.value = videos
    }

    // 更新下载进度
    fun refreshDownloadProgress(downloadInfo: ConcurrentMap<String, RecordFilesInfo.RecordFile>) {
        var cacheList = getCacheVideos()
        cacheList.forEach {
            if (downloadInfo.containsKey(it.downloadUrl)) {
                it.downloadDstPath = downloadInfo.get(it.downloadUrl)!!.downloadDstPath
                it.downloadProgress = downloadInfo.get(it.downloadUrl)!!.downloadProgress
            }
        }
        transmissionVideos.value = cacheList
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(cacheList))
    }

    // 保存视频记录
    fun saveCacheVideo(data: RecordFilesInfo.RecordFile) {
        var cacheList = getCacheVideos()
        cacheList.add(0, data)
        transmissionVideos.value = cacheList
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(cacheList))
    }

    // 删除某一条视频记录
    fun deleteCacheVideo(data: RecordFilesInfo.RecordFile) {
        var cacheList = getCacheVideos()
        val list = cacheList.filter {
            it.rtspUrl != data.rtspUrl
        }
        transmissionVideos.value = list.toMutableList()
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(transmissionVideos.value))
    }

    // 清除全部传输记录
    fun clearAllCacheVideos() {
        transmissionVideos.value = arrayListOf<RecordFilesInfo.RecordFile>()
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(transmissionVideos.value))
    }
}