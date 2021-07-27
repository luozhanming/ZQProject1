package cn.com.ava.zqproject.ui.createmeeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.zqproject.vo.ContractUser

class CreateMeetingViewModel : BaseViewModel() {

    companion object {
        const val MAX_SELECTED_USER = 10
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


}