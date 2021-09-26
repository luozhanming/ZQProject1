package cn.com.ava.zqproject.ui.createmeeting

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.common.CanLoadMore
import cn.com.ava.zqproject.ui.common.CanRefresh
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.LoadMoreState
import cn.com.ava.zqproject.vo.RefreshState
import cn.com.ava.zqproject.vo.StatefulView
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class ContractBookViewModel : BaseViewModel(), CanRefresh, SelectedUser ,CanLoadMore{


    val contractUsers: MutableLiveData<List<StatefulView<ContractUser>>> by lazy {
        val data = arrayListOf<StatefulView<ContractUser>>()
        val livedata = MutableLiveData<List<StatefulView<ContractUser>>>()
        livedata.value = data
        livedata
    }


    override val refreshState: MutableLiveData<RefreshState> by lazy {
        MutableLiveData()
    }

    override val loadMoreState: MutableLiveData<LoadMoreState> by lazy {
        MutableLiveData()
    }

    override val mSelectedUsers: MutableList<ContractUser> by lazy {
        arrayListOf()
    }

    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    var curPage: Int = 0

    val filterUser: MutableLiveData<List<StatefulView<ContractUser>>> by lazy {
        MediatorLiveData<List<StatefulView<ContractUser>>>().apply {
            value = arrayListOf()
            addSource(searchKey) { key ->
                if (key.isNotEmpty()) {
                    searchKey()
                }
            }
        }
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
                    stateful.isSelected = mSelectedUsers.contains(it)
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


    fun searchKey() {
        mDisposables.add(PlatformApi.getService()
            .getContractUsers(searchKey = searchKey.value ?: "", pageSize = 100)
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
                filterUser.postValue(it)
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
                if (!TextUtils.isEmpty(searchKey.value)) {   //过滤的
                    val filterUsers = filterUser.value
                    filterUsers?.forEach {
                        it.isSelected = mSelectedUsers.contains(it.obj)
                    }
                    filterUser.postValue(filterUser.value)
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