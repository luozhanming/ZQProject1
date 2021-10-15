package cn.com.ava.zqproject.ui.videoResource

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.util.GsonUtil
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.entity.TransmissionProgressEntity
import cn.com.ava.lubosdk.manager.VideoResourceManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.common.CanRefresh
import cn.com.ava.zqproject.ui.videoResource.service.VideoSingleton
import cn.com.ava.zqproject.vo.RefreshState
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.ConcurrentMap
import kotlin.collections.ArrayList

class VideoManageViewModel : BaseViewModel, CanRefresh {

    constructor(): super() {
        initData()
    }

    fun initData() {

//        val cacheStr = VideoPreference.getElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, "")
//        if (cacheStr != null && cacheStr.length != 0) {
//            cacheVideos = Gson().fromJson<ArrayList<RecordFilesInfo.RecordFile>>(cacheStr, object : TypeToken<ArrayList<RecordFilesInfo.RecordFile>>(){}.type)
//        }
//        // 获取本地缓存视频记录
        transmissionVideos.value = VideoSingleton.cacheVideos
    }

    override val refreshState: MutableLiveData<RefreshState> by lazy {
        MutableLiveData()
    }

    val isLoading: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    // 列表资源
    val videoResources: MutableLiveData<MutableList<StatefulView<RecordFilesInfo.RecordFile>>> by lazy {
        val data = arrayListOf<StatefulView<cn.com.ava.lubosdk.entity.RecordFilesInfo.RecordFile>>()
        val liveData = MutableLiveData<MutableList<StatefulView<RecordFilesInfo.RecordFile>>>()
        liveData.value = data
        liveData
    }
    // 本地缓存记录
//    var cacheVideos: ArrayList<RecordFilesInfo.RecordFile> = arrayListOf()

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

    // 上传列表的fileName集合
//    val uploadFileNames: MutableLiveData<MutableList<String>> by lazy {
//        val data = MutableLiveData<MutableList<String>>()
//        val list = arrayListOf<String>()
//        data.value = list
//        data
//    }

    // 默认按录制时间降序
    var sortIndex = 1

    // 搜索过滤后的视频
    val filterVideos: MediatorLiveData<List<StatefulView<RecordFilesInfo.RecordFile>>> by lazy {
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

    // 检测是否有缓存
    fun checkCacheResult(data: RecordFilesInfo.RecordFile): Boolean {
//        logd("本地视频: ${cacheList.toString()}")
        VideoSingleton.cacheVideos.forEach {
            if (data.rawFileName == it.rawFileName && data.transmissionType == it.transmissionType) {
                return true
            }
        }
        return false
    }

    // 获取录播视频
    fun getVideoResourceList() {
        mDisposables.add(
            VideoResourceManager.getRecordFileList()
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
                    logPrint2File(it,"VideoManageViewModel#getVideoResourceList")
                    logd("视频资源请求失败")
                    refreshState.postValue(RefreshState(true, true))
                })
        )


//        mDisposables.add(
//            GeneralManager.loadRecordFiles()
//            .compose(PlatformApi.applySchedulers())
//            .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ list ->
//                refreshState.postValue(RefreshState(true, false))
//                val statefuls = arrayListOf<StatefulView<RecordFilesInfo.RecordFile>>()
//                list.forEach {
//                    val stateful = StatefulView(it)
//                    statefuls.add(stateful)
//                }
//                videoResources.value = statefuls
//                logd("视频资源请求成功")
//                logd("时间排序")
//                if (sortIndex == 1) { // 录制时间降序
//                    sortByRecordTime()
//                } else { // 文件大小降序
//                    sortByFileSize()
//                }
//            }, {
//                logPrint2File(it)
//                logd("视频资源请求失败")
//                refreshState.postValue(RefreshState(true, true))
//            })
//        )
    }

//    // 删除视频
//    fun deleteVideo(video: RecordFilesInfo.RecordFile) {
//        isLoading.postValue(OneTimeEvent(true))
//        mDisposables.add(
//            VideoResourceManager.deleteRecordFile2(video)
//                .compose(PlatformApi.applySchedulers())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ result ->
//                    if (result == true) {
//                        logd("删除视频资源成功")
//                        if (mSelectedVideos?.contains(video) == true) {
//                            mSelectedVideos.remove(video)
//                        }
//                        if (mSelectedVideos.size == 0) {
//                            isLoading.postValue(OneTimeEvent(false))
//                            ToastUtils.showShort("删除成功")
//                        }
//                        val videos = videoResources.value
//                        val stateful = StatefulView(video)
//                        videos?.remove(stateful)
//                        videoResources.value = videos
//
//                        if (!TextUtils.isEmpty(searchKey.value)) { // 更新搜索视频列表
//                            val list = videoResources.value
//                            val filter = list?.filter {
//                                return@filter it.obj.downloadFileName.contains(searchKey.value ?: "")
//                            }
//                            filterVideos.value = filter
//                        }
//                    } else {
//                        isLoading.postValue(OneTimeEvent(false))
//                        ToastUtils.showShort("删除失败")
//                        logd("删除视频资源失败")
//                    }
//                }, {
//                    isLoading.postValue(OneTimeEvent(false))
//                    logPrint2File(it,"VideoManageViewModel#deleteVideo")
//                    logd("删除视频资源接口出错")
//                })
//        )
//    }

    // 删除视频
    fun deleteVideo(videos: List<RecordFilesInfo.RecordFile>) {
        isLoading.postValue(OneTimeEvent(true))
        mDisposables.add(
            VideoResourceManager.deleteMultiRecordFiles(videos)
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (result == true) {
                        logd("删除视频资源成功")

                        val list = videoResources.value

                        videos.forEach {
                            val stateful = StatefulView(it)
                            list?.remove(stateful)
                            if (mSelectedVideos?.contains(it) == true) {
                                mSelectedVideos.remove(it)
                            }
                        }

                        if (mSelectedVideos.size == 0) {
                            isLoading.postValue(OneTimeEvent(false))
                            ToastUtils.showShort("删除成功")
                        }

                        videoResources.value = list

                        if (!TextUtils.isEmpty(searchKey.value)) { // 更新搜索视频列表
                            val filter = list?.filter {
                                return@filter it.obj.downloadFileName.contains(searchKey.value ?: "")
                            }
                            filterVideos.value = filter
                        }
                    } else {
                        isLoading.postValue(OneTimeEvent(false))
                        ToastUtils.showShort("删除失败")
                        logd("删除视频资源失败")
                    }
                }, {
                    isLoading.postValue(OneTimeEvent(false))
                    logPrint2File(it,"VideoManageViewModel#deleteVideo")
                    logd("删除视频资源接口出错")
                })
        )
    }

    // 更新下载进度
    fun refreshDownloadProgress(downloadInfo: ConcurrentMap<String, RecordFilesInfo.RecordFile>) {
        VideoSingleton.cacheVideos.forEach {
            if (downloadInfo.containsKey(it.downloadUrl) && it.transmissionType == 1) {
                it.downloadDstPath = downloadInfo.get(it.downloadUrl)!!.downloadDstPath
                it.downloadProgress = downloadInfo.get(it.downloadUrl)!!.downloadProgress
                logd("下载进度：${it.downloadProgress}")
            }
        }
        transmissionVideos.value = VideoSingleton.cacheVideos
    }

    /*
    * 更新上传进度
    * */
    fun refreshUploadProgress(uploadInfo: ConcurrentMap<String, TransmissionProgressEntity>) {
        VideoSingleton.cacheVideos.forEach {
            if (uploadInfo.containsKey(it.rawFileName) && it.transmissionType == 2) {
                it.uploadState = uploadInfo.get(it.rawFileName)!!.state
                it.uploadProgress = uploadInfo.get(it.rawFileName)!!.progress
                logd("上传进度：${it.uploadProgress}")
                if (it.uploadState == 1) { // 上传成功
                    ToastUtils.showShort(it.downloadFileName + getResources().getString(R.string.tip_upload_success))
                } else if (it.uploadState == 3) { // 上传失败
                    ToastUtils.showShort(it.downloadFileName + getResources().getString(R.string.tip_upload_failed))
                }
            }
        }
        transmissionVideos.postValue(VideoSingleton.cacheVideos)
    }

    // 保存视频记录
    fun saveCacheVideo(list: MutableList<RecordFilesInfo.RecordFile>) {
        VideoSingleton.cacheVideos.addAll(0, list)
        transmissionVideos.value = VideoSingleton.cacheVideos
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(VideoSingleton.cacheVideos))
    }

    // 删除某一条视频记录
    fun deleteCacheVideo(data: RecordFilesInfo.RecordFile) {
        VideoSingleton.cacheVideos.remove(data)
        transmissionVideos.value = VideoSingleton.cacheVideos
        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(transmissionVideos.value))
    }

    // 清除全部传输记录
    fun clearAllCacheVideos() {
        VideoSingleton.cacheVideos = arrayListOf<RecordFilesInfo.RecordFile>()
        transmissionVideos.value = VideoSingleton.cacheVideos
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

    /*
    * 上传视频
    * */
    fun uploadVideo(video: RecordFilesInfo.RecordFile, desc: String) {


//        val ftpInfo = PlatformApiManager.getApiPath(PlatformApiManager.PATH_FTP_INFO)
//        logd("ftpInfo = $ftpInfo")
//
//        mDisposables.add(
//            VideoResourceManager.uploadRecordFile2(getMap(ftpInfo) ?: HashMap(), video)
//                .compose(PlatformApi.applySchedulers())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ result ->
//                    if (result == true) {
//                        logd("上传视频资源提交成功")
//                        ToastUtils.showShort("正在上传" + video.downloadFileName)
//                        video.transmissionType = 2
//                        saveCacheVideo(arrayListOf(video))
//
//                        val list = uploadFileNames.value
//                        if (list?.contains(video.rawFileName) == false) {
//                            list.add(0, video.rawFileName)
//                            uploadFileNames.postValue(list)
//                        }
//                    } else {
//                        ToastUtils.showShort("上传失败，请重试")
//                        logd("上传视频资源提交失败")
//                    }
//                }, {
//                    logPrint2File(it)
//                    logd("上传视频资源接口出错")
//                    ToastUtils.showShort("上传失败，请重试")
//                })
//        )
    }

    /*
    * 获取上传列表
    * */
    fun getUploadList() {
        mDisposables.add(
            VideoResourceManager.getUploadList()
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({

                }, {
                    logPrint2File(it,"VideoManageViewModel#getUploadList")
                    logd("获取上传列表接口出错")
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        filterVideos.removeSource(searchKey)
        filterVideos.value = null
        videoResources.value = null
    }




}