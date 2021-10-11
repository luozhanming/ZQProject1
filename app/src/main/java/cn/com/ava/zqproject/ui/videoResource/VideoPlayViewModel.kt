package cn.com.ava.zqproject.ui.videoResource

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.VolumeChannel
import cn.com.ava.lubosdk.manager.GeneralManager
import io.reactivex.schedulers.Schedulers
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

class VideoPlayViewModel : BaseViewModel() {

    // 当前播放进度
    val currentDuration: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            value = 0
        }
    }

    val isControlVisible: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = true
        }
    }

    val masterVolume: MutableLiveData<VolumeChannel> by lazy {
        MutableLiveData()
    }

    fun changeVolume(volume: Int) {
        mDisposables.add(GeneralManager.setMasterChannelVolume(volume)
            .subscribeOn(Schedulers.io())
            .subscribe({

            }, {
                logPrint2File(it,"VideoPlayViewModel#changeVolume")
            })
        )
    }

    fun getVolumeInfo() {
        mDisposables.add(GeneralManager.getVolumeChannelInfoV2()
            .map {
                it.firstOrNull {
                    it.channelName == "MASTER"
                }
            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it !=  null) {
                    masterVolume.postValue(it)
                }
            }, {
                logPrint2File(it,"VideoPlayViewModel#getVolumeInfo")
            })
        )
    }

    fun getCurrentTime(time: Int): String {
//        val currentTime = currentDuration.value
        var hour = 0
        var min = 0
        var second = 0
        hour = time!! / 3600
        min = (time - hour * 3600) / 60
        second = time - hour * 3600 - min * 60
//        logd("当前时间: " + String.format("%02d:%02d:%02d", hour, min, second))
        return String.format("%02d:%02d:%02d", hour, min, second)
    }

}