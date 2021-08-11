package cn.com.ava.zqproject.ui.record

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.entity.RecordInfo
import cn.com.ava.lubosdk.entity.VolumeChannel
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.lubosdk.manager.RecordManager
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.zqproject.common.ComputerModeManager
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class RecordViewModel : BaseViewModel() {


    val recordInfo: MutableLiveData<RecordInfo> by lazy {
        MutableLiveData()
    }

    val isShowLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val isControlVisible: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = true
        }
    }

    val isRecording: MediatorLiveData<Boolean> by lazy {
        MediatorLiveData<Boolean>().apply {
            addSource(recordInfo) { info ->
                val isRecording = value ?: false
                val newRecording = info.recordState == Constant.RECORD_RECORDING
                if (isRecording != newRecording) {
                    value = newRecording
                }
            }
        }
    }

    val isPluginComputer: MediatorLiveData<Boolean> by lazy {
        MediatorLiveData<Boolean>().apply {
            addSource(recordInfo) { info ->
                val outputWindow = info.currentOutputWindow
                val isPlugin = ComputerModeManager.isPluginComputer(outputWindow)
                if (isPlugin && value == null) {
                    val mode = ComputerModeManager.getComputerMode(outputWindow)
                    ComputerModeManager.setCurComputerMode(mode)
                }
                if (isPlugin != value) {
                    postValue(isPlugin)
                }
                //记录上次非电脑模式
                if (!isPlugin) {
                    lastNonComputerWindow = outputWindow
                }
            }
        }
    }

    val masterVolume: MutableLiveData<VolumeChannel> by lazy {
        MutableLiveData()
    }

    /**
     * 上次不是电脑的画面
     */
    private var lastNonComputerWindow: String = "V1"


    private var mLoadRecordInfoLoop: Disposable? = null

    fun startLoadRecordInfo() {
        isShowLoading.postValue(true)
        mLoadRecordInfoLoop?.dispose()
        mLoadRecordInfoLoop = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                RecordManager.getRecordInfo()
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribe({
                isShowLoading.postValue(false)
                recordInfo.postValue(it)
            }, {
                logPrint2File(it)
                isShowLoading.postValue(false)
            })


    }


    override fun onCleared() {
        super.onCleared()
        mLoadRecordInfoLoop?.dispose()
        mLoadRecordInfoLoop = null
        //防止内存泄漏
        isRecording.removeSource(recordInfo)
    }

    fun toggleRecord() {
        val curState = recordInfo.value?.recordState ?: Constant.RECORD_STOP
        var nextState = when (curState) {
            Constant.RECORD_STOP -> Constant.RECORD_RECORDING
            else -> Constant.RECORD_STOP
        }
        mDisposables.add(
            RecordManager.controlRecord(nextState)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
                })
        )
    }

    fun toggleLive() {
        mDisposables.add(
            RecordManager.controlLiving(recordInfo.value?.isLiving?.not() ?: false)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
                })
        )
    }

    fun togglePause() {
        val curState = recordInfo.value?.recordState ?: Constant.RECORD_RECORDING
        var nextState = when (curState) {
            Constant.RECORD_PAUSE -> Constant.RECORD_RESUME
            else -> Constant.RECORD_PAUSE
        }
        mDisposables.add(
            RecordManager.controlRecord(nextState)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
                })
        )
    }

    fun setTrackMode(mode: String) {
        mDisposables.add(
            RecordManager.setCamTrackMode(mode)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
                })
        )
    }

    /**
     * 开关电脑画面
     * */
    fun toggleComputer() {
        if (isPluginComputer.value == true) {  //当前是电脑
            mDisposables.add(
                WindowLayoutManager.setPreviewLayout(lastNonComputerWindow)
                    .subscribeOn(Schedulers.io())
                    .subscribe({

                    }, {
                        logPrint2File(it)
                    })
            )
        } else {
            mDisposables.add(
                WindowLayoutManager.setPreviewLayout(ComputerModeManager.getCurModeWindowCmd())
                    .subscribeOn(Schedulers.io())
                    .subscribe({

                    }, {
                        logPrint2File(it)
                    })
            )
        }
    }


    fun changeComputer() {
        if (isPluginComputer.value == true) {
            mDisposables.add(
                WindowLayoutManager.setPreviewLayout(ComputerModeManager.getCurModeWindowCmd())
                    .subscribeOn(Schedulers.io())
                    .subscribe({

                    }, {
                        logPrint2File(it)
                    })
            )
        }

    }

    fun changeVolume(volume: Int) {
        mDisposables.add(GeneralManager.setMasterChannelVolume(volume)
            .subscribeOn(Schedulers.io())
            .subscribe({

            },{
                logPrint2File(it)
            }))
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
                if (it != null) {
                    masterVolume.postValue(it)
                }
            }, {
                logPrint2File(it)
            })
        )
    }


}