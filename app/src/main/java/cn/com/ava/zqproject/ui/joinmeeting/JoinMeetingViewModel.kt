package cn.com.ava.zqproject.ui.joinmeeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.manager.ZQManager
import cn.com.ava.zqproject.net.PlatformApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class JoinMeetingViewModel : BaseViewModel() {
    /**
     * 会议号
     * */
    val meetingNum: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    /**
     * 会议密码
     * */
    val meetingPsw: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    /**
     * 入会昵称
     * */
    val meetingNickname: MutableLiveData<String> = MutableLiveData<String>().apply {
        val platformLogin = PlatformApi.getPlatformLogin()
        platformLogin?.apply {
            value = if (professionTitleName?.isNotEmpty() == true) {
                "${this.name}_${this.professionTitleName}"
            } else {
                "${this.name}"
            }
        }
    }


    val isLoading: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    val goListener: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }


    fun joinMeeting() {
        val meetingnum = meetingNum.value ?: ""
        val meetingpsw = meetingPsw.value ?: ""
        val nickname = meetingNickname.value ?: ""
        isLoading.postValue(OneTimeEvent(true))
        mDisposables.add(ZQManager.joinMeeting(meetingnum, meetingpsw, "", nickname)
            .subscribeOn(Schedulers.io())
            .subscribe({
                if(it){
                    checkJoinResult()
                }else{
                   //失败
                    isLoading.postValue(OneTimeEvent(false))
                    goListener.postValue(OneTimeEvent(false))
                }
            }, {
                isLoading.postValue(OneTimeEvent(false))
                goListener.postValue(OneTimeEvent(false))
                logPrint2File(it,"JoinMeetingViewModel#joinMeeting")
            })
        )

    }

    private fun checkJoinResult() {
        mDisposables.add(Observable.intervalRange(0,5,0,1000,TimeUnit.MILLISECONDS)
            .doOnComplete {
                isLoading.postValue(OneTimeEvent(false))
                goListener.postValue(OneTimeEvent(false))
            }
            .flatMap {
                ZQManager.loadMeetingInfo()
                    .flatMap {info->
                        if(info.confMode=="cloudCtrlMode"){
                            return@flatMap  ZQManager.loadMeetingMember()
                                .map {
                                    if(it.localRole!=4){
                                        info.confMode = "classMode"
                                        return@map true
                                    }
                                      return@map false
                                }
                        }else{
                            return@flatMap Observable.just(false)
                        }
                    }
            }.subscribeOn(Schedulers.io())
            .subscribe({
                if(it){
                    isLoading.postValue(OneTimeEvent(false))
                    goListener.postValue(OneTimeEvent(true))
                }
            },{
                isLoading.postValue(OneTimeEvent(false))
                logPrint2File(it,"JoinMeetingViewModel#checkJoinResult")
            }))
    }


}