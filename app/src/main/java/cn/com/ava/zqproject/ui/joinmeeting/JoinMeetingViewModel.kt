package cn.com.ava.zqproject.ui.joinmeeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel

class JoinMeetingViewModel:BaseViewModel() {

    val meetingNum:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val meetingPsw:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val meetingNickname:MutableLiveData<String> by lazy {
        MutableLiveData()
    }


}