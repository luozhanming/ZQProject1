package cn.com.ava.zqproject.ui.videoResource

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.common.CanRefresh
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.RefreshState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class VideoManageViewModel : BaseViewModel(), CanRefresh {

    override val refreshState: MutableLiveData<RefreshState> by lazy {
        MutableLiveData()
    }

    val videoResources: MutableLiveData<List<RecordFilesInfo.RecordFile>> by lazy {
        val data = arrayListOf<cn.com.ava.lubosdk.entity.RecordFilesInfo.RecordFile>()
        val liveData = MutableLiveData<List<RecordFilesInfo.RecordFile>>()
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
        val sortList = list?.sortedByDescending { video -> video.rawFileSize }
        videoResources.value = sortList
    }

    // 根据录制时间排序
    fun sortByRecordTime() {
        // rawDuration
        val list = videoResources.value
        // 指定以 rawDuration 属性进行降序排序
        val sortList = list?.sortedByDescending { video -> video.recordRawBeginTime }
        videoResources.value = sortList
    }

    fun downloadVideo(video: RecordFilesInfo.RecordFile) {
        val list = transmissionVideos.value
        if (list?.contains(video) == false) {
            list?.add(video)
            transmissionVideos.value = list
            logd("已添加到传输列表")
        } else {
            logd("已下载")
        }
    }

    fun getVideoResourceList() {
        mDisposables.add(
            GeneralManager.loadRecordFiles()
            .compose(PlatformApi.applySchedulers())
            .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                refreshState.postValue(RefreshState(true, false))
                videoResources.postValue(it)
                logd("视频资源请求成功")
//                logd(it.toString())
//                it.forEach {
//                    logd("downloadURL:" + it.downloadUrl + ", rtspUrl: " + it.rtspUrl)
//                }
//                for (info in it) {
//                    logd("downloadURL:" + info.downloadUrl + ", rtspUrl: " + info.rtspUrl)
//                }
            }, {
                logPrint2File(it)
                logd("视频资源请求失败")
                refreshState.postValue(RefreshState(true, true))
            })
        )
    }
}