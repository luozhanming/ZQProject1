package cn.com.ava.zqproject.ui.videoResource

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.common.CanRefresh
import cn.com.ava.zqproject.vo.RecordFilesInfo
import cn.com.ava.zqproject.vo.RefreshState
import io.reactivex.schedulers.Schedulers

class VideoResourceListViewModel : BaseViewModel(), CanRefresh {

    override val refreshState: MutableLiveData<RefreshState> by lazy {
        MutableLiveData()
    }

    val videoResources: MutableLiveData<List<cn.com.ava.lubosdk.entity.RecordFilesInfo.RecordFile>> by lazy {
        val data = arrayListOf<cn.com.ava.lubosdk.entity.RecordFilesInfo.RecordFile>()
        val liveData = MutableLiveData<List<cn.com.ava.lubosdk.entity.RecordFilesInfo.RecordFile>>()
        liveData.value = data
        liveData
    }

    fun getVideoResourceList() {
        mDisposables.add(GeneralManager.loadRecordFiles()
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
                logPrint2File(it,"VideoResourceListViewModel#getVideoResourceList")
                logd("视频资源请求失败")
                refreshState.postValue(RefreshState(true, true))
            })
        )
    }
}