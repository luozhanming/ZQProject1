package cn.com.ava.zqproject.ui.videoResource

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
            .subscribe({
                refreshState.postValue(RefreshState(true, false))
                videoResources.postValue(it)
                logd("视频资源请求成功")
                for (info in it) {
                    logd("downloadURL:" + info.downloadUrl + ", rtspUrl: " + info.rtspUrl)
                }
            }, {
                logPrint2File(it)
                logd("视频资源请求失败")
                refreshState.postValue(RefreshState(true, true))
            })
        )
    }
}