package cn.com.ava.zqproject.ui.home

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.entity.LuBoInfo
import cn.com.ava.lubosdk.manager.GeneralManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * 任务：1.轮询录播查询当前互动状态，如果是会议中的，弹出弹框提示，让其跳入会议（重启TP9场景)
 *
 *
 */
class HomeViewModel : BaseViewModel() {


    private val luboInfo: MutableLiveData<LuBoInfo> by lazy {
        MutableLiveData()
    }

    private var mLoadLuboInfoDisposable: Disposable? = null

    fun startloadLuboInfo() {
        mLoadLuboInfoDisposable = Flowable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                GeneralManager.getLuboInfo()
                    .retryWhen(RetryFunction(Int.MAX_VALUE))
                    .toFlowable(BackpressureStrategy.LATEST)
            }.subscribe({
                luboInfo.postValue(it)
            }, {
                logPrint2File(it)
            })
    }

    fun stopLoadLuboInfo() {
        mLoadLuboInfoDisposable?.dispose()
    }





}
