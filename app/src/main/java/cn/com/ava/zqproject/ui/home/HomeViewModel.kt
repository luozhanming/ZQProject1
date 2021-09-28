package cn.com.ava.zqproject.ui.home

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.http.ResponseThrowable
import cn.com.ava.common.http.ServerException
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.LuBoInfo
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.lubosdk.manager.ZQManager
import cn.com.ava.lubosdk.zq.entity.MeetingInfoZQ
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.InvitationInfo
import cn.com.ava.zqproject.vo.PlatformLogin
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * 任务：1.轮询录播查询当前互动状态，如果是会议中的，弹出弹框提示，让其跳入会议（重启TP9场景)
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

    /**
     * 会议信息
     * */
    val meetingInfoZq: OneTimeLiveData<MeetingInfoZQ> by lazy {
        OneTimeLiveData()
    }

    val isLoading:OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    /**
     * 振铃信息
     * */
    val invitationInfo: OneTimeLiveData<InvitationInfo> by lazy {
        OneTimeLiveData()
    }

    val backToLogin:OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    private var mLoadLuboInfoDisposable: Disposable? = null
    private var mSendHeartBeatDisposable: Disposable? = null
    private var mLoopMeetingInfoZQDisposable: Disposable? = null
    private var mLoopMeetingInvitationDisposable: Disposable? = null


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


    fun startLoopMeetingInvitation() {
        mLoopMeetingInvitationDisposable?.dispose()
        mLoopMeetingInvitationDisposable = Observable.interval(5000, TimeUnit.MILLISECONDS)
            .flatMap {
                PlatformApi.getService().queryCalledMeeting()
            }.subscribeOn(Schedulers.io())
            .subscribe({
                //TODO 没响应
                if (it.success) {
                    //振铃了，立刻到振铃界面
                    if (it.data.isNotEmpty()) {
                        invitationInfo.postValue(OneTimeEvent(it.data[0]))
                    }
                }

            }, {
                logPrint2File(it)
            })
    }

    fun stopLoopMeetingInvitation() {
        mLoopMeetingInvitationDisposable?.dispose()
    }


    /**
     * 开始发送心跳
     * */
    fun startHeartBeat() {
        mSendHeartBeatDisposable?.dispose()
        mSendHeartBeatDisposable = Observable.interval(0, 20, TimeUnit.SECONDS)
            .flatMap {
                PlatformApi.getService().heartBeat(rsAcct = luboInfo.value?.stun?.usr ?: "")
                    .compose(PlatformApi.applySchedulers())
            }
            .subscribe({
                it.toString()
            }, {
                if(it is ServerException){  //有人异地登录
                    if(it.code==10004){
                        ToastUtils.showShort(getResources().getString(R.string.toast_other_login_exception))
                        backToLogin.postValue(OneTimeEvent(true))
                    }
                }
                logPrint2File(it)
                //发送心跳错误
            })
    }


    /**
     * 停止发送心跳
     * */
    fun stopHeartBeat() {
        mSendHeartBeatDisposable?.dispose()
    }

    fun preloadWindowAndLayout() {
        mDisposables.add(
            WindowLayoutManager.getPreviewWindowInfo()
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
                })
        )
        mDisposables.add(
            WindowLayoutManager.getLayoutButtonInfo()
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
                })
        )
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

    fun startLoopMeetingInfoZQ() {
        mLoopMeetingInfoZQDisposable?.dispose()
        mLoopMeetingInfoZQDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                ZQManager.loadMeetingInfo()
                    .flatMap {info->
                        if(info.confMode=="cloudCtrlMode"){
                         return@flatMap   ZQManager.loadMeetingMember()
                                .map {
                                    if(it.localRole!=4){
                                        info.confMode = "classMode"
                                    }
                                    return@map info
                                }
                        }else{
                            return@flatMap Observable.just(info)
                        }
                    }
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                meetingInfoZq.postValue(OneTimeEvent(it))
            }, {
                logPrint2File(it)
            })
    }


    fun stopLoopMeetingInfoZQ() {
        mLoopMeetingInfoZQDisposable?.dispose()

    }


}
