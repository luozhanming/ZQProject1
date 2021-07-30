package cn.com.ava.zqproject.ui.record

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.entity.RecordInfo
import cn.com.ava.lubosdk.manager.RecordManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
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

    val isControlVisible:MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = true
        }
    }

    private var mLoadRecordInfoLoop: Disposable? = null

    fun startLoadRecordInfo() {
        isShowLoading.postValue(true)
        mLoadRecordInfoLoop?.dispose()
        mLoadRecordInfoLoop = Flowable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                RecordManager.getRecordInfo()
                    .retryWhen(RetryFunction(Int.MAX_VALUE))
                    .toFlowable(BackpressureStrategy.LATEST)
            }.subscribe({
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
    }

    fun toggleRecord() {
        val curState = recordInfo.value?.recordState?:Constant.RECORD_STOP
        var nextState = when(curState){
            Constant.RECORD_STOP ->Constant.RECORD_RECORDING
            else -> Constant.RECORD_STOP
        }
        mDisposables.add(RecordManager.controlRecord(nextState)
            .subscribeOn(Schedulers.io())
            .subscribe({

            },{
                logPrint2File(it)
            }))
    }

    fun togglePause() {
        val curState = recordInfo.value?.recordState?:Constant.RECORD_RECORDING
        var nextState = when(curState){
            Constant.RECORD_PAUSE ->Constant.RECORD_RESUME
            else -> Constant.RECORD_PAUSE
        }
        mDisposables.add(RecordManager.controlRecord(nextState)
            .subscribeOn(Schedulers.io())
            .subscribe({

            },{
                logPrint2File(it)
            }))
    }

    fun toggleLive() {
        mDisposables.add(RecordManager.controlLiving(recordInfo.value?.isLiving?.not()?:false)
            .subscribeOn(Schedulers.io())
            .subscribe({

            },{
                logPrint2File(it)
            }))
    }
}