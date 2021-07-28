package cn.com.ava.zqproject.ui.createmeeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.common.CanRefresh
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.RefreshState
import cn.com.ava.zqproject.vo.StatefulView
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class ContractBookViewModel : BaseViewModel() ,CanRefresh,SelectedUser{


    val contractUsers: MutableLiveData<List<StatefulView<ContractUser>>> by lazy {
        val data = arrayListOf<StatefulView<ContractUser>>()
        val livedata = MutableLiveData<List<StatefulView<ContractUser>>>()
        livedata.value = data
        livedata
    }


    override val refreshState: MutableLiveData<RefreshState> by lazy {
        MutableLiveData()
    }

    override val mSelectedUsers: MutableList<ContractUser> by lazy {
        arrayListOf()
    }


    fun getContractUserList() {
        mDisposables.add(PlatformApi.getService().getContractUsers()
            .compose(PlatformApi.applySchedulers())
            .map {
                val statefuls = arrayListOf<StatefulView<ContractUser>>()
                it.data.forEach {
                    val stateful = StatefulView(it)
                    stateful.isSelected = mSelectedUsers.contains(it)
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


    fun setSelectedUsers(users: List<ContractUser>) {
        mSelectedUsers.clear()
        mSelectedUsers.addAll(users)
        //更新现有的状态，算法仍待考虑
        mDisposables.add(
            Observable.create<Boolean> { emitter ->
                val filter = contractUsers.value
                filter?.forEach {
                    it.isSelected = mSelectedUsers.contains(it.obj)
                }
                val value = contractUsers.value
                contractUsers.postValue(contractUsers.value)
                emitter.onNext(true)

            }
                .subscribeOn(Schedulers.computation())
                .subscribe({


                },{
                    logPrint2File(it)
                })
        )
    }
}