package cn.com.ava.zqproject.ui.createmeeting

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.common.CanRefresh
import cn.com.ava.zqproject.vo.ContractGroup
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.RefreshState
import cn.com.ava.zqproject.vo.StatefulView
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class ContractGroupViewModel : BaseViewModel(), CanRefresh, SelectedUser {


    val contractGroups: MutableLiveData<List<StatefulView<ContractGroup>>> by lazy {
        MutableLiveData<List<StatefulView<ContractGroup>>>().apply {
            value = emptyList()
        }
    }

    override val refreshState: MutableLiveData<RefreshState> by lazy {
        MutableLiveData()
    }

    override val mSelectedUsers: MutableList<ContractUser> by lazy {
        arrayListOf()
    }

    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val filterUser: MutableLiveData<List<StatefulView<ContractGroup>>> by lazy {
        MediatorLiveData<List<StatefulView<ContractGroup>>>().apply {
            value = arrayListOf()
            addSource(searchKey) { key ->
                //关键词过滤规则
                val list = contractGroups.value
                val filter = list?.filter {
                    val group = it.obj
                    return@filter (!TextUtils.isEmpty(group.name) && group.name.contains(key))
                }
                postValue(filter)
            }
        }
    }


    fun getContractGroups() {
        mDisposables.add(
            PlatformApi.getService().getContractGroups()
                .compose(PlatformApi.applySchedulers())
                .map {
                    val statefuls = arrayListOf<StatefulView<ContractGroup>>()
                    val selectedUserIds = mSelectedUsers.map { contract ->
                        contract.userId
                    }
                    it.data.forEach {
                        val stateful = StatefulView(it)
                        val selected = it.userIdList.all { user ->
                            selectedUserIds.contains(user.userId)
                        }
                        stateful.isSelected = selected
                        statefuls.add(stateful)
                    }
                    statefuls
                }
                .subscribe({
                    refreshState.postValue(RefreshState(true, false))
                    contractGroups.postValue(it)
                }, {
                    refreshState.postValue(RefreshState(true, true))
                    logPrint2File(it)
                })
        )
    }

    fun setSelectedUser(users: MutableList<ContractUser>) {
        mSelectedUsers.clear()
        mSelectedUsers.addAll(users)
        val filter = filterUser.value

        //更新现有的状态，算法仍待考虑,运算量巨大，多的时候
        mDisposables.add(
            Observable.create<Boolean> { emitter ->
                val groups = contractGroups.value
                groups?.forEach {
                    val group = it.obj
                    //如果都在选择里面的话
                    it.isSelected = group.userIdList.all { groupUser ->
                        mSelectedUsers.contains(groupUser)
                    }
                }
                contractGroups.postValue(groups)
                if (!TextUtils.isEmpty(searchKey.value)) {   //过滤的
                    filter?.forEach {
                        val group = it.obj
                        //如果都在选择里面的话
                        it.isSelected = group.userIdList.all { groupUser ->
                            mSelectedUsers.contains(groupUser)
                        }
                    }
                    filterUser.postValue(filter)
                }
            }
                    .subscribeOn(Schedulers.computation())
                    .subscribe({
                    }, {
                        logPrint2File(it)
                    })
                )
            }


    }