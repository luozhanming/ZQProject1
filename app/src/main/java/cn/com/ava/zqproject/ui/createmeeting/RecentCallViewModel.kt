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

class RecentCallViewModel : BaseViewModel(),CanRefresh,SelectedUser {


    val recentContract: MutableLiveData<List<StatefulView<ContractUser>>> by lazy {
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





    fun getRecentContracts() {
        mDisposables.add(PlatformApi.getService().getRecentCall()
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
                recentContract.postValue(it)
            }, {
                logPrint2File(it)
                refreshState.postValue(RefreshState(true, true))
            })
        )
    }

    fun setSelectedUsers(users: MutableList<ContractUser>) {
        mSelectedUsers.clear()
        mSelectedUsers.addAll(users)
        //更新现有的状态，算法仍待考虑
        mDisposables.add(
            Observable.create<Boolean> { emitter ->
                val filter = recentContract.value
                filter?.forEach {
                    it.isSelected = mSelectedUsers.contains(it.obj)
                }
                recentContract.postValue(recentContract.value)
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