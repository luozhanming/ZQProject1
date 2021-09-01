package cn.com.ava.zqproject.ui.meeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.zqproject.vo.MeetingMember

class MemeberManagerViewModel:BaseViewModel() {


    /**
     * 已入会成员
     * */
    val onMeetingMembers:MutableLiveData<List<MeetingMember>> by lazy {
        MutableLiveData()
    }
    /**
     * 等待室成员
     * */
    val onWaitingMembers:MutableLiveData<List<MeetingMember>> by lazy {
        MutableLiveData()
    }



    fun getOnMeetingMembers() {
        val datas = arrayListOf<MeetingMember>()
        datas.add(MeetingMember(LinkedUser(),"sdf",true,true))
        datas.add(MeetingMember(LinkedUser(),"qweqwe",false,true))
        datas.add(MeetingMember(LinkedUser(),"werwer",true,false))
        onMeetingMembers.postValue(datas)

    }

    fun getWaitingMembers() {
        val datas = arrayListOf<MeetingMember>()
        datas.add(MeetingMember(LinkedUser(),"sdf",true,true))
        datas.add(MeetingMember(LinkedUser(),"qweqwe",false,true))
        datas.add(MeetingMember(LinkedUser(),"werwer",true,false))
        onWaitingMembers.postValue(datas)
    }


}