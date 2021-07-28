package cn.com.ava.zqproject.ui.createmeeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.RefreshState
import cn.com.ava.zqproject.vo.StatefulView
import io.reactivex.schedulers.Schedulers

class ContractBookViewModel : BaseViewModel() {


    val contractUsers: MutableLiveData<List<StatefulView<ContractUser>>> by lazy {
        MutableLiveData()
    }


    val refreshState: MutableLiveData<RefreshState> by lazy {
        MutableLiveData()
    }

    val mSelectedUsers: MutableList<ContractUser> by lazy {
        arrayListOf()
    }


    fun getContractUserList() {
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
                refreshState.postValue(RefreshState(true, false))
                contractUsers.postValue(it)
            }, {
                logPrint2File(it)
                refreshState.postValue(RefreshState(true, true))
            })
        )
    }


    fun setSelectedUsers(users:List<ContractUser>){
        mSelectedUsers.clear()
        mSelectedUsers.addAll(users)
    }
}