package cn.com.ava.zqproject.ui.createmeeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.zqproject.vo.ContractGroup
import cn.com.ava.zqproject.vo.ContractUser

class CreateMeetingViewModel : BaseViewModel() {

    companion object {
        const val MAX_SELECTED_USER = 9
    }

    val selectedUser: MutableLiveData<MutableList<ContractUser>> by lazy {
        val data = MutableLiveData<MutableList<ContractUser>>()
        val list = arrayListOf<ContractUser>()
        data.value = list
        data
    }


    fun addOrDelSelectedUser(user: ContractUser): Boolean {
        val list = selectedUser.value
        if (list?.contains(user) == true) {
            list.remove(user)
            selectedUser.value = list
            return true
        } else {
            if (list?.size ?: 0 < MAX_SELECTED_USER) {
                list?.add(user)
                selectedUser.value = list
                return true
            } else {
                return false
            }

        }
    }

    fun addOrDelGroup(group: ContractGroup): Boolean {
        val userIdList = group.userIdList
        val selectedUsers = selectedUser.value
        //1.判断所有人是否在已选
        val allInSelected = userIdList.all {
            selectedUsers?.contains(it) ?: false
        }
        if (allInSelected) {   //移除选择
            userIdList?.forEach {
                selectedUsers?.remove(it)
            }
            selectedUser.value = selectedUsers
            return true
        } else {   //有部分没有添加
            val unAddedUsers = arrayListOf<ContractUser>()
            userIdList?.forEach {
                if (selectedUsers?.contains(it) == false) {
                    unAddedUsers.add(it)
                }
            }
            val preCount = (selectedUsers?.size ?: 0) + unAddedUsers.size
            if (preCount< MAX_SELECTED_USER) {
                selectedUsers?.addAll(unAddedUsers)
                selectedUser.value = selectedUsers
                return true
            } else {  //满人了
                return false
            }

        }

    }

    /**
     * 创建会议
     * @param theme 会议主题
     * @param nickname 入会称呼
     * @param waiting 是否开启等候室
     * */
    fun createMeeting(theme: String, nickname: String, waiting: Boolean) {
        TODO()

    }


}