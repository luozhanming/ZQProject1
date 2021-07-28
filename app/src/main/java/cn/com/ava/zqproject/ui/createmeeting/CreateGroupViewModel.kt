package cn.com.ava.zqproject.ui.createmeeting

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.ContractGroup
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import io.reactivex.schedulers.Schedulers

class CreateGroupViewModel : BaseViewModel() {

    companion object {
        const val MAX_GROUP_CONTRACT_COUNT = 10
    }

    val contractUsers: MutableLiveData<List<StatefulView<ContractUser>>> by lazy {
        val data = arrayListOf<StatefulView<ContractUser>>()
        val livedata = MutableLiveData<List<StatefulView<ContractUser>>>()
        livedata.value = data
        livedata
    }


    val selectedUser: MutableLiveData<MutableList<ContractUser>> by lazy {
        val data = arrayListOf<ContractUser>()
        val livedata = MutableLiveData<MutableList<ContractUser>>()
        livedata.value = data
        livedata
    }


    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val filterUser: MutableLiveData<List<StatefulView<ContractUser>>> by lazy {
        MediatorLiveData<List<StatefulView<ContractUser>>>().apply {
            value = arrayListOf()
            addSource(searchKey) { key ->
                val list = contractUsers.value
                val filter = list?.filter {
                    val user = it.obj
                    return@filter (!TextUtils.isEmpty(user.userName) && user.userName.contains(key)) ||
                            (!TextUtils.isEmpty(user.professionTitleName) && user.professionTitleName.contains(
                                key
                            ))
                }
                postValue(filter)
            }
        }
    }

    val createdGroup: MutableLiveData<ContractGroup> by lazy {
        MutableLiveData()
    }


    fun getContractUser() {
        mDisposables.add(PlatformApi.getService().getContractUsers()
            .compose(PlatformApi.applySchedulers())
            .map {
                val statefuls = arrayListOf<StatefulView<ContractUser>>()
                it.data.forEach {
                    val stateful = StatefulView(it)
                    statefuls.add(stateful)
                }
                statefuls
            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                contractUsers.postValue(it)
            }, {
                logPrint2File(it)
            })
        )
    }

    fun addOrDelContractUser(user: ContractUser): Boolean {
        val list = selectedUser.value
        val contracts = contractUsers.value
        val filter = filterUser.value
        if (list?.contains(user) == true) {
            list.remove(user)
            selectedUser.value = list
            val stateView = contracts?.firstOrNull { it.obj == user }
            stateView?.isSelected = false
            contractUsers.value = contractUsers.value
            if (!TextUtils.isEmpty(searchKey.value)) {   //过滤的
                val stateView1 = filter?.firstOrNull { it.obj == user }
                stateView1?.isSelected = false
                filterUser.value = filterUser.value
            }
            return true
        } else {
            if (list?.size ?: 0 < MAX_GROUP_CONTRACT_COUNT) {
                list?.add(user)
                selectedUser.value = list
                val stateView = contracts?.firstOrNull { it.obj == user }
                stateView?.isSelected = true
                contractUsers.value = contractUsers.value
                if (!TextUtils.isEmpty(searchKey.value)) {   //过滤的
                    val stateView1 = filter?.firstOrNull { it.obj == user }
                    stateView1?.isSelected = true
                    filterUser.value = filterUser.value
                }
                return true
            } else {  //满人了
                return false
            }
        }
    }

    fun createMeeting(name: String) {
        val users = selectedUser.value?.map {
            it.userId
        }
        val buffer = StringBuffer().apply {
            append("[")
            users?.forEachIndexed { index, user ->
                append('"')
                append(user)
                append('"')
                if (index != user.length - 1) {
                    append(",")
                }
            }
            append("]")
        }
        mDisposables.add(
            PlatformApi.getService()
                .editGroup(name = name, userIdJson = buffer.toString())
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    ToastUtils.showShort(Utils.getApp().getString(R.string.create_success))
                    createdGroup.postValue(it.data)
                }, {
                    ToastUtils.showShort(Utils.getApp().getString(R.string.create_failed))
                    logPrint2File(it)
                })
        )
    }


}