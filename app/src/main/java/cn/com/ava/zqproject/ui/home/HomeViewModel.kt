package cn.com.ava.zqproject.ui.home

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.entity.LuBoInfo
import cn.com.ava.lubosdk.manager.*
import cn.com.ava.lubosdk.zq.entity.MeetingInfoZQ
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.eventbus.GoLoginEvent
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.InvitationInfo
import cn.com.ava.zqproject.vo.PlatformLogin
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
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

    val isLoading: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    /**
     * 振铃信息
     * */
    val invitationInfo: OneTimeLiveData<InvitationInfo> by lazy {
        OneTimeLiveData()
    }

    val backToLogin: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }


    val goCreateMeeting: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    val goJoinMeeting: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    private var mLoadLuboInfoDisposable: Disposable? = null
    private var mLoopMeetingInfoZQDisposable: Disposable? = null
    private var mLoopMeetingInvitationDisposable: Disposable? = null


    fun startloadLuboInfo() {
        mLoadLuboInfoDisposable?.dispose()
        mLoadLuboInfoDisposable = Observable.interval(0, 5, TimeUnit.SECONDS)
            .flatMap {
                GeneralManager.getLuboInfo()
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                luboInfo.postValue(it)
            }, {
                logPrint2File(it, "HomeViewModel#startloadLuboInfo")
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
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it.success) {
                    //振铃了，立刻到振铃界面
                    if (it.data.isNotEmpty()) {
                        invitationInfo.postValue(OneTimeEvent(it.data[0]))
                    }
                }

            }, {
                logPrint2File(it, "HomeViewModel#startLoopMeetingInvitation")
            })
    }

    fun stopLoopMeetingInvitation() {
        mLoopMeetingInvitationDisposable?.dispose()
    }


    /**
     * 开始发送心跳
     * */
    fun startHeartBeat() {
        mDisposables.add(Observable.interval(
            0, 20, TimeUnit.SECONDS
        ).flatMap {
                PlatformApi.getService()
                    .heartBeat(rsAcct = LoginManager.getLogin()?.rserverInfo?.usr ?: "")
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribe({
                if (it.code == 10004) {
                    ToastUtils.showShort(getResources().getString(R.string.toast_other_login_exception))
                    EventBus.getDefault().post(GoLoginEvent())
                }
            }, {
                logPrint2File(it, "HomeViewModel#startHeartBeat")
                //发送心跳错误
            })
        )
    }


    fun preloadWindowAndLayout() {
        mDisposables.add(
            WindowLayoutManager.getPreviewWindowInfo()
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it, "HomeViewModel#preloadWindowAndLayout1")
                })
        )
        mDisposables.add(
            WindowLayoutManager.getLayoutButtonInfo()
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it, "HomeViewModel#preloadWindowAndLayout2")
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
                    logPrint2File(it, "HomeViewModel#logout")
                })
        )
    }

    /**
     * 轮询token跟新
     * */
    fun loopRefreshToken() {
        mDisposables.add(Observable.interval(0, 1, TimeUnit.MINUTES)
                .flatMap {
                    PlatformApi.getService().refreshToken()
                        .compose(PlatformApi.applySchedulers())
                }.retryWhen(RetryFunction(Int.MAX_VALUE))
                .subscribe({
                    PlatformApi.refreshLoginToken(it.data)
                    logd(it.toString())
                }, {
                    logPrint2File(it, "HomeViewModel#loopRefreshToken")
                })
        )
    }

    fun startLoopMeetingInfoZQ() {
        mLoopMeetingInfoZQDisposable?.dispose()
        mLoopMeetingInfoZQDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                ZQManager.loadMeetingInfo()
                    .flatMap { info ->
                        if (info.confMode == "cloudCtrlMode") {
                            return@flatMap ZQManager.loadMeetingMember()
                                .map {
                                    if (it.localRole != 4) {
                                        info.confMode = "classMode"
                                    }
                                    return@map info
                                }
                        } else {
                            return@flatMap Observable.just(info)
                        }
                    }
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                meetingInfoZq.postValue(OneTimeEvent(it))
            }, {
                logPrint2File(it, "HomeViewModel#startLoopMeetingInfoZQ")
            })
    }


    fun stopLoopMeetingInfoZQ() {
        mLoopMeetingInfoZQDisposable?.dispose()

    }

    /**
     * 请求是否能去互动   0创建会议  1加入会议
     * */
    fun requestCanCreateMeeting(createOrJoin: Int) {
        mDisposables.add(RecordManager.getRecordInfo()
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it.isLiving || it.recordState != Constant.RECORD_STOP) {
                    ToastUtils.showShort(getResources().getString(R.string.toast_cannot_go_create_meeting))
                } else {
                    if (createOrJoin == 0) {
                        goCreateMeeting.postValue(OneTimeEvent(true))
                    } else {
                        goJoinMeeting.postValue(OneTimeEvent(true))
                    }
                }
            }, {
                logPrint2File(it, "HomeViewModel#requestCanCreateMeeting")
            })
        )
    }


    fun autoLuboLogin() {
        mDisposables.add(Observable.interval(3, TimeUnit.MINUTES)
            .flatMap {
                LoginManager.newLogin(
                    LoginManager.getLogin()?.username ?: "",
                    LoginManager.getLogin()?.password ?: ""
                )
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({

            }, {
                logPrint2File(it, "HomeViewModel#autoLuboLogin")
            })
        )
    }


}
