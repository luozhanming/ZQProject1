package cn.com.ava.zqproject.ui.joinmeeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.manager.ZQManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.InvitationInfo
import io.reactivex.schedulers.Schedulers

class ReceiveCallViewModel:BaseViewModel() {

    /**
     * 步骤
     * */
    val step:MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            value = 1
        }
    }


    val invitationInfo:MutableLiveData<InvitationInfo> by lazy {
        MutableLiveData()
    }


    /**
     * 呼叫者所在单位
     * */
    val callerUnit:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    /**
     * 入会称呼
     * */
    val callerNickname:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val isLoading:OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    val joinSuccess:OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }


    /**
     * 我的入会昵称
     * */
    val myNickname:MutableLiveData<String> by lazy {
        MutableLiveData<String>().apply {
            val platformLogin = PlatformApi.getPlatformLogin()
            if(platformLogin?.professionTitleName?.isNotEmpty()==true){
                value = "${platformLogin?.name}_${platformLogin?.professionTitleName}"
            }else{
                value = "${platformLogin?.name}"
            }
        }
    }


    fun joinMeeting() {
        isLoading.postValue(OneTimeEvent(true))
        val value = invitationInfo.value
        value?.apply {
            mDisposables.add(ZQManager.joinMeeting(value.confNo,"",value.ticket,myNickname.value?:"")
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if(it){
                        isLoading.postValue(OneTimeEvent(false))
                        joinSuccess.postValue(OneTimeEvent(true))
                    }
                },{
                    isLoading.postValue(OneTimeEvent(false))
                    logPrint2File(it)
                }))
        }

    }
}