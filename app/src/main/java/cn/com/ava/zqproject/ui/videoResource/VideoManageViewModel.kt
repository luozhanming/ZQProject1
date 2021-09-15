package cn.com.ava.zqproject.ui.videoResource

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.storage.StorageVolume
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ReportFragment
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.GsonUtil
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.lubosdk.manager.VideoResourceManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.common.CanRefresh
import cn.com.ava.zqproject.ui.videoResource.service.VideoSingleton
import cn.com.ava.zqproject.usb.DownloadObject
import cn.com.ava.zqproject.usb.UsbHelper
import cn.com.ava.zqproject.vo.RefreshState
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ConcurrentMap
import kotlin.concurrent.thread

class VideoManageViewModel : BaseViewModel, CanRefresh {

    constructor(): super() {
        initData()
    }

    fun initData() {

        val cacheStr = VideoPreference.getElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, "")
        if (cacheStr != null && cacheStr.length != 0) {
            cacheVideos = Gson().fromJson<ArrayList<RecordFilesInfo.RecordFile>>(cacheStr, object : TypeToken<ArrayList<RecordFilesInfo.RecordFile>>(){}.type)
        }
        // 获取本地缓存视频记录
        transmissionVideos.value = cacheVideos
    }

    override val refreshState: MutableLiveData<RefreshState> by lazy {
        MutableLiveData()
    }
    // 列表资源
    val videoResources: MutableLiveData<MutableList<StatefulView<RecordFilesInfo.RecordFile>>> by lazy {
        val data = arrayListOf<StatefulView<cn.com.ava.lubosdk.entity.RecordFilesInfo.RecordFile>>()
        val liveData = MutableLiveData<MutableList<StatefulView<RecordFilesInfo.RecordFile>>>()
        liveData.value = data
        liveData
    }
    // 本地缓存记录
    var cacheVideos: ArrayList<RecordFilesInfo.RecordFile> = arrayListOf()

//    val transmissionVideos = MutableLiveData<MutableList<RecordFilesInfo.RecordFile>>()
    // 传输列表资源
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

    // 默认按录制时间降序
    var sortIndex = 1

    // 搜索过滤后的视频
    val filterVideos: MutableLiveData<List<StatefulView<RecordFilesInfo.RecordFile>>> by lazy {
        MediatorLiveData<List<StatefulView<RecordFilesInfo.RecordFile>>>().apply {
            //value = arrayListOf()
            addSource(searchKey) { key ->
                // 关键词过滤规则
                val list = videoResources.value
                val filter = list?.filter {
                    return@filter (it.obj.downloadFileName.contains(key))
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
        list?.sortByDescending { video -> video.obj.rawFileSize }
        videoResources.value = list
    }

    // 根据录制时间排序
    fun sortByRecordTime() {
        // rawDuration
        val list = videoResources.value
        // 指定以 rawDuration 属性进行降序排序
        list?.sortByDescending { video -> video.obj.recordRawBeginTime }
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
//        var cacheList = getCacheVideos()

//        logd("本地视频: ${cacheList.toString()}")

        cacheVideos.forEach {
            if (data.downloadUrl == it.downloadUrl) {
                if (it.downloadProgress < 100) {
//                    ToastUtils.showShort("该视频正在下载中")
                } else {
//                    ToastUtils.showShort("该视频已下载完成")
                }
                return true
            }
        }
        return false
    }

    // 获取本地缓存的视频
//    fun getCacheVideos(): ArrayList<RecordFilesInfo.RecordFile> {
////        return VideoSingleton.getInstance().cacheVideos
//
//        val cacheVideos = VideoPreference.getElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, "")
//        var cacheList = arrayListOf<RecordFilesInfo.RecordFile>()
//        if (cacheVideos != null && cacheVideos.length != 0) {
//            cacheList = Gson().fromJson<ArrayList<RecordFilesInfo.RecordFile>>(cacheVideos, object : TypeToken<ArrayList<RecordFilesInfo.RecordFile>>(){}.type)
//        }
////        logd("本地视频: ${cacheList.toString()}")
//        return cacheList
//    }

    val handler = Handler(Looper.getMainLooper())

    // 获取录播视频
    fun getVideoResourceList() {
        mDisposables.add(
            GeneralManager.loadRecordFiles()
            .compose(PlatformApi.applySchedulers())
            .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                refreshState.postValue(RefreshState(true, false))
                val statefuls = arrayListOf<StatefulView<RecordFilesInfo.RecordFile>>()
                list.forEach {
                    val stateful = StatefulView(it)
                    statefuls.add(stateful)
                }
                videoResources.value = statefuls
                logd("视频资源请求成功")
                logd("时间排序")
                if (sortIndex == 1) { // 录制时间降序
                    sortByRecordTime()
                } else { // 文件大小降序
                    sortByFileSize()
                }
            }, {
                logPrint2File(it)
                logd("视频资源请求失败")
                refreshState.postValue(RefreshState(true, true))
            })
        )
    }

    // 删除视频
    fun deleteVideo(video: RecordFilesInfo.RecordFile) {
        mDisposables.add(
            VideoResourceManager.deleteRecordFile(video)
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (result == true) {
                        logd("删除视频资源成功")
                        if (mSelectedVideos?.contains(video) == true) {
                            mSelectedVideos.remove(video)
                        }
                        if (mSelectedVideos.size == 0) {
                            ToastUtils.showShort("删除成功")
                        }
                        val videos = videoResources.value
                        val stateful = StatefulView(video)
                        videos?.remove(stateful)
                        videoResources.value = videos
                    } else {
                        ToastUtils.showShort("删除失败")
                        logd("删除视频资源失败")
                    }
                }, {
                    logPrint2File(it)
                    logd("删除视频资源接口出错")
                })
        )
    }

    // 更新下载进度
    fun refreshDownloadProgress(downloadInfo: ConcurrentMap<String, RecordFilesInfo.RecordFile>) {
        cacheVideos.forEach {
            if (downloadInfo.containsKey(it.downloadUrl)) {
                it.downloadDstPath = downloadInfo.get(it.downloadUrl)!!.downloadDstPath
                it.downloadProgress = downloadInfo.get(it.downloadUrl)!!.downloadProgress
                logd("下载进度：${it.downloadProgress}")
            }
        }
        transmissionVideos.value = cacheVideos
//        VideoSingleton.getInstance().cacheVideos = cacheVideos
//        logd("VideoSingleton.getInstance()，cacheList = ${cacheVideos.toString()}")

//        var cacheList = VideoSingleton.getInstance().cacheVideos
//        cacheList.forEach {
//            if (downloadInfo.containsKey(it.downloadUrl)) {
//                it.downloadDstPath = downloadInfo.get(it.downloadUrl)!!.downloadDstPath
//                it.downloadProgress = downloadInfo.get(it.downloadUrl)!!.downloadProgress
//                logd("下载进度：${it.downloadProgress}")
//            }
//        }
//        transmissionVideos.value = cacheList
//        VideoSingleton.getInstance().cacheVideos = cacheList
//        logd("VideoSingleton.getInstance()，cacheList = ${VideoSingleton.getInstance().cacheVideos.toString()}")

//        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(cacheList))


//        var cacheList = getCacheVideos()
//        cacheList.forEach {
//            if (downloadInfo.containsKey(it.downloadUrl)) {
//                it.downloadDstPath = downloadInfo.get(it.downloadUrl)!!.downloadDstPath
//                it.downloadProgress = downloadInfo.get(it.downloadUrl)!!.downloadProgress
//            }
//        }
//        transmissionVideos.value = cacheList
//        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(cacheList))
    }

    // 保存视频记录
    fun saveCacheVideo(list: MutableList<RecordFilesInfo.RecordFile>) {
        cacheVideos.addAll(0, list)
        transmissionVideos.value = cacheVideos
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(cacheVideos))

//        var cacheList = VideoSingleton.getInstance().cacheVideos
//        cacheList.addAll(0, list)
//        transmissionVideos.value = cacheList
//        VideoSingleton.getInstance().cacheVideos = cacheList
//        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(cacheList))

//        var cacheList = getCacheVideos()
//        cacheList.add(0, data)
//        transmissionVideos.value = cacheList
//        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(cacheList))
    }

    // 删除某一条视频记录
    fun deleteCacheVideo(data: RecordFilesInfo.RecordFile) {
        val list = cacheVideos.filter {
            it.rtspUrl != data.rtspUrl
        }
        transmissionVideos.value = list.toMutableList()
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(transmissionVideos.value))


//        var cacheList = VideoSingleton.getInstance().cacheVideos
//        val list = cacheList.filter {
//            it.rtspUrl != data.rtspUrl
//        }
//        transmissionVideos.value = list.toMutableList()
//        VideoSingleton.getInstance().cacheVideos = list as ArrayList<RecordFilesInfo.RecordFile>
//        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(transmissionVideos.value))


//        var cacheList = getCacheVideos()
//        val list = cacheList.filter {
//            it.rtspUrl != data.rtspUrl
//        }
//        transmissionVideos.value = list.toMutableList()
//        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(transmissionVideos.value))
    }

    // 清除全部传输记录
    fun clearAllCacheVideos() {
        transmissionVideos.value = arrayListOf<RecordFilesInfo.RecordFile>()
        cacheVideos = arrayListOf<RecordFilesInfo.RecordFile>()
//        VideoSingleton.getInstance().cacheVideos = arrayListOf<RecordFilesInfo.RecordFile>()

        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(transmissionVideos.value))
    }

    // 是否处于全选状态
    val isSelectedAll: MutableLiveData<Boolean> by lazy {
        val liveData = MutableLiveData<Boolean>()
        liveData.value = false
        liveData
    }

    val mSelectedVideos: MutableList<RecordFilesInfo.RecordFile> by lazy {
        arrayListOf()
    }

    fun selectedAllOrCancel(isCancel: Boolean) {
        val list = videoResources.value
        mSelectedVideos.clear()
        list?.forEach {
            it.isSelected = !isCancel
            if (isCancel == false) {
                mSelectedVideos.add(it.obj)
            }
        }
        videoResources.value = list
        isSelectedAll.value = !isCancel
    }

    fun addOrDelSelectedVideo(video: RecordFilesInfo.RecordFile) {
        if (mSelectedVideos?.contains(video) == true) {
            mSelectedVideos.remove(video)
        } else {
            mSelectedVideos?.add(video)
        }
    }
}