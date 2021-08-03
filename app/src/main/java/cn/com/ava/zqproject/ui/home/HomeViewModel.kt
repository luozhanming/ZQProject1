package cn.com.ava.zqproject.ui.home

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.entity.LuBoInfo
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.PlatformLogin
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * 任务：1.轮询录播查询当前互动状态，如果是会议中的，弹出弹框提示，让其跳入会议（重启TP9场景)
 *
 *
 */
class HomeViewModel : BaseViewModel() {


    val luboInfo: MutableLiveData<LuBoInfo> by lazy {
        MutableLiveData()
    }

    val platformLogin:MutableLiveData<PlatformLogin> by lazy {
        MutableLiveData<PlatformLogin>().apply {
            value = PlatformApi.getPlatformLogin()
        }
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
        mLoadLuboInfoDisposable = null
    }

    fun preloadWindowAndLayout(){
        WindowLayoutManager.getPreviewWindowInfo()
            .subscribeOn(Schedulers.io())
            .subscribe({

            },{
                logPrint2File(it)
            })
        WindowLayoutManager.getLayoutButtonInfo()
            .subscribeOn(Schedulers.io())
            .subscribe({

            },{
                logPrint2File(it)
            })
        ComputerModeManager.getComputerIndex()
    }





}
