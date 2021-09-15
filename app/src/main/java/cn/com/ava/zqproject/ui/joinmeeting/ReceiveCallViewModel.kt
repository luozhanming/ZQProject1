package cn.com.ava.zqproject.ui.joinmeeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.zqproject.net.PlatformApi

class ReceiveCallViewModel:BaseViewModel() {


    /**
     * 步骤
     * */
    val step:MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            value = 1
        }
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


    /**
     * 我的入会昵称
     * */
    val myNickname:MutableLiveData<String> by lazy {
        MutableLiveData<String>().apply {
            val platformLogin = PlatformApi.getPlatformLogin()
            value = "${platformLogin?.name}-${platformLogin?.professionTitleName}"
        }
    }
}