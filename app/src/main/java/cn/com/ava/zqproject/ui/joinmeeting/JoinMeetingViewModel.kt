package cn.com.ava.zqproject.ui.joinmeeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel

class JoinMeetingViewModel:BaseViewModel() {
    /**
     * 会议号
     * */
    val meetingNum:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    /**
     * 会议密码
     * */
    val meetingPsw:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    /**
     * 入会昵称
     * */
    val meetingNickname:MutableLiveData<String> by lazy {
        MutableLiveData()
    }





}