package cn.com.ava.zqproject.ui.record

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.entity.RecordInfo
import cn.com.ava.lubosdk.manager.RecordManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class RecordViewModel:BaseViewModel() {


    private val recordInfo:MutableLiveData<RecordInfo> by lazy {
        MutableLiveData()
    }

    fun startLoadRecordInfo(){
        mDisposables.add(Flowable.interval(1000,TimeUnit.MILLISECONDS)
            .flatMap {
                RecordManager.getRecordInfo()
                    .retryWhen(RetryFunction(Int.MAX_VALUE))
                    .toFlowable(BackpressureStrategy.LATEST)
            }.subscribe({
                recordInfo.postValue(it)
            },{
                logPrint2File(it)
            }))
    }
}