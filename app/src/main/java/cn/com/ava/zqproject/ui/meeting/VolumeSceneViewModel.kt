package cn.com.ava.zqproject.ui.meeting

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.entity.VolumeChannel
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.zqproject.vo.CamPreviewInfo
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class VolumeSceneViewModel : BaseViewModel() {

    val camPreviewInfo: MutableLiveData<CamPreviewInfo> by lazy {
        MutableLiveData()
    }

    val volumeChannels:MutableLiveData<List<VolumeChannel>> by lazy {
        MutableLiveData()
    }



    fun loadCamPreviewInfo() {
        mDisposables.add(
                    InteracManager.getSceneStream(true).map {
                        val windows = Cache.getCache().windowsCache
                        val cams = windows.filter {
                            it.isPtz
                        }
                        val curOutput = it.firstOrNull { it.isMainOutput }
                        cams.forEach {
                            it.isCurrentOutput = it.index==curOutput?.windowIndex?:-1
                        }
                        val info = CamPreviewInfo(
                            if (it.size <= 5) 1 else 2,
                            if (it.size <= 5) it.size else 5,
                            cams.size,
                            cams,
                            curOutput?.windowIndex ?: -1
                        )
                        info
                    }.subscribeOn(Schedulers.io())

           .subscribe({
            camPreviewInfo.postValue(it)
        }, {
            logPrint2File(it)
        }))
    }

    fun loadVolumeChannels(){
        mDisposables.add(
            InteracManager.getInteracVolumeChannels()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    volumeChannels.postValue(it)
                },{
                    logPrint2File(it)
                })
        )
    }

    fun setAudioLevel(channelName: String, level: Int) {
        mDisposables.add(
            InteracManager.setInteracVolumeChannels(channelName,level)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    loadVolumeChannels()
                },{
                    logPrint2File(it)
                })
        )
    }

    fun postMainStream(i: Int) {
        mDisposables.add(
            InteracManager.postMainStream(i)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    loadCamPreviewInfo()
                },{
                    logPrint2File(it)
                })
        )
    }
}