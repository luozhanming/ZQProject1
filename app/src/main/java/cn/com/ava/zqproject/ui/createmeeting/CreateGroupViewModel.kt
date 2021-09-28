package cn.com.ava.zqproject.ui.createmeeting

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.common.CanLoadMore
import cn.com.ava.zqproject.ui.common.CanRefresh
import cn.com.ava.zqproject.vo.*
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import io.reactivex.schedulers.Schedulers

class CreateGroupViewModel : BaseViewModel() , CanRefresh, CanLoadMore {

    companion object {
        const val MAX_GROUP_CONTRACT_COUNT = 9
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



    var curPage: Int = 0

    override val refreshState: MutableLiveData<RefreshState> by lazy {
        MutableLiveData()
    }

    override val loadMoreState: MutableLiveData<LoadMoreState> by lazy {
        MutableLiveData()
    }


    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    val filterUser: MutableLiveData<List<StatefulView<ContractUser>>> by lazy {
        MediatorLiveData<List<StatefulView<ContractUser>>>().apply {
            value = arrayListOf()
            addSource(searchKey) { key ->
                if (key.isNotEmpty()) {
                    searchKeyUser()
                }
            }
        }
    }

    private fun searchKeyUser() {
        mDisposables.add(PlatformApi.getService()
            .getContractUsers(searchKey = searchKey.value ?: "", pageSize = 100)
            .compose(PlatformApi.applySchedulers())
            .map {
                val statefuls = arrayListOf<StatefulView<ContractUser>>()
                it.data.forEach {
                    val stateful = StatefulView(it)
                    stateful.isSelected = selectedUser.value?.contains(it)?:false
                    statefuls.add(stateful)
                }
                statefuls
            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                refreshState.postValue(RefreshState(true, false))
                filterUser.postValue(it)
            }, {
                logPrint2File(it)
                refreshState.postValue(RefreshState(true, true))
            })
        )
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


    fun getContractUserList(isLoadMore: Boolean) {
        var page = 1
        if (isLoadMore) {
            page = curPage + 1
        }
        mDisposables.add(PlatformApi.getService().getContractUsers(pageIndex = page)
            .compose(PlatformApi.applySchedulers())
            .map {
                //更新页面
                curPage = it.query.pageIndex
                val statefuls = arrayListOf<StatefulView<ContractUser>>()
                it.data.forEach {
                    val stateful = StatefulView(it)
                    stateful.isSelected = selectedUser.value?.contains(it)?:false
                    statefuls.add(stateful)
                }
                statefuls
            }
            .subscribeOn(Schedulers.io())
            .subscribe({

                if(!isLoadMore){
                    contractUsers.postValue(it)
                    refreshState.postValue(RefreshState(true, false))
                }else{
                    loadMoreState.postValue(LoadMoreState(true, false))
                    val list = mutableListOf<StatefulView<ContractUser>>()
                    list.addAll(contractUsers.value?: emptyList())
                    list.addAll(it)
                    contractUsers.postValue(list)
                }
            }, {
                logPrint2File(it)
                if(isLoadMore){
                    loadMoreState.postValue(LoadMoreState(true, true))
                }else{
                    refreshState.postValue(RefreshState(true, true))
                }

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
                filterUser.postValue(filterUser.value)
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