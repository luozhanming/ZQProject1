package cn.com.ava.zqproject.ui.home

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.LuBoInfo
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.PlatformLogin
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
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

    val platformLogin: MutableLiveData<PlatformLogin> by lazy {
        MutableLiveData<PlatformLogin>().apply {
            value = PlatformApi.getPlatformLogin()
        }
    }

    val logout: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    private var mLoadLuboInfoDisposable: Disposable? = null
    private var mSendHeartBeatDisposable:Disposable?=null


    fun startloadLuboInfo() {
        mLoadLuboInfoDisposable?.dispose()
        mLoadLuboInfoDisposable = Observable.interval(5, TimeUnit.SECONDS)
            .flatMap {
                GeneralManager.getLuboInfo()
                    .retryWhen(RetryFunction(Int.MAX_VALUE))
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


    /**
     * 开始发送心跳
     * */
    fun startHeartBeat(){
        mSendHeartBeatDisposable?.dispose()
        mSendHeartBeatDisposable = Observable.interval(0,20, TimeUnit.SECONDS)
            .flatMap {
               PlatformApi.getService().heartBeat(rsAcct = luboInfo.value?.stun?.usr?:"")
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribe({
                it.toString()
            }, {
                logPrint2File(it)
                //发送心跳错误
            })
    }


    /**
     * 停止发送心跳
     * */
    fun stopHeartBeat(){
        mSendHeartBeatDisposable?.dispose()
    }

    fun preloadWindowAndLayout() {
        mDisposables.add(WindowLayoutManager.getPreviewWindowInfo()
            .subscribeOn(Schedulers.io())
            .subscribe({

            }, {
                logPrint2File(it)
            }))
        mDisposables.add(WindowLayoutManager.getLayoutButtonInfo()
            .subscribeOn(Schedulers.io())
            .subscribe({

            }, {
                logPrint2File(it)
            }))
        ComputerModeManager.getComputerIndex()
    }


    fun logout() {
        mDisposables.add(
            PlatformApi.getService()
                .logout(token = PlatformApi.getPlatformLogin()?.token ?: "")
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    PlatformApi.logout()
                }, {
                    logPrint2File(it)
                })
        )
    }

    /**
     * 轮询token跟新
     * */
    fun loopRefreshToken() {
        mDisposables.add(
            Observable.interval(0, 2, TimeUnit.MINUTES)
                .flatMap {
                    PlatformApi.getService().refreshToken()
                        .compose(PlatformApi.applySchedulers())
                }
                .subscribe({
                    PlatformApi.refreshLoginToken(it.data)
                    logd(it.toString())
                }, {
                    logPrint2File(it)
                })
        )
    }


}
