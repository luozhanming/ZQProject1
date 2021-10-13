package cn.com.ava.zqproject.ui.meeting

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.entity.VolumeChannel
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.zqproject.vo.CamPreviewInfo
import io.reactivex.schedulers.Schedulers

class VolumeSceneViewModel : BaseViewModel() {

    val camPreviewInfo: MutableLiveData<CamPreviewInfo> by lazy {
        MutableLiveData()
    }

    val volumeChannels: MutableLiveData<List<VolumeChannel>> by lazy {
        MutableLiveData()
    }

    val tabIndex: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    /**
     * 是否有Mic3
     * */
    val isVolumein5Visible: MediatorLiveData<Boolean> by lazy {
        MediatorLiveData<Boolean>().apply {
            addSource(volumeChannels) {
                if (it.size < 6) {
                    postValue(false)
                } else {
                    val hasMic3 = !it[5].channelName.isNullOrEmpty()
                    postValue(hasMic3 && tabIndex.value == 1)
                }
            }

        }
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
                    it.isCurrentOutput = it.index == curOutput?.windowIndex ?: -1
                }
                val info = CamPreviewInfo(
                    if (windows.size <= 6) 1 else 2,
                    if (windows.size <= 6) windows.size else 5,
                    cams.size,
                    cams,
                    curOutput?.windowIndex ?: -1
                )
                info
            }.subscribeOn(Schedulers.io())

                .subscribe({
                    if (it.curOutputIndex < 0 || it.curOutputIndex > it.camCount)
                        it.curOutputIndex = 0
                    camPreviewInfo.postValue(it)
                }, {
                    logPrint2File(it)
                })
        )
    }

    fun loadVolumeChannels() {
        mDisposables.add(
            InteracManager.getInteracVolumeChannels()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val filter = mutableListOf<VolumeChannel>()
                    val array =
                        arrayOf("MASTER", "LINEIN1", "LINEIN2", "MICIN1", "MICIN2", "MICIN3")
                    filter.addAll(it.filter {
                        it.channelName in array
                    })
                    if (filter.size < 6) {   //没有MICIN3
                        filter.add(VolumeChannel())
                    }
                    volumeChannels.postValue(filter)
                }, {
                    logPrint2File(it)
                })
        )
    }

    fun setAudioLevel(channelName: String, level: Int) {
        mDisposables.add(
            InteracManager.setInteracVolumeChannels(channelName, level)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    loadVolumeChannels()
                }, {
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
                }, {
                    logPrint2File(it)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        isVolumein5Visible.removeSource(volumeChannels)
    }
}