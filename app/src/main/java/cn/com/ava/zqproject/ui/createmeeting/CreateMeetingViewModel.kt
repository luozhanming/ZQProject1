package cn.com.ava.zqproject.ui.createmeeting

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.manager.ZQManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.ContractGroup
import cn.com.ava.zqproject.vo.ContractUser
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

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

    val goMeeting:OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    val isLoading:OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
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
            if (preCount <= MAX_SELECTED_USER) {
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

        val users = arrayListOf<String>().apply {
            val selected = selectedUser.value
            selected?.forEach {
                add(it.userId)
            }
        }
        isLoading.postValue(OneTimeEvent(true))
        mDisposables.add(ZQManager.createMeeting(theme, "", nickname, waiting, arrayOf())
            .delay(5000,TimeUnit.MILLISECONDS)
            .flatMap {
                if (it) {
                    ZQManager.loadMeetingInfo()   //加载会议信息
                        .flatMap {
                            return@flatMap PlatformApi.getService().createMeeting(  //回传平台
                                initiator = nickname,
                                meetingTitle = theme,
                                meetingNo = it.confId,
                                userId = users
                            )
                                .compose(PlatformApi.applySchedulers())
                                .flatMap {
                                    if(it.success){  //成功
                                        return@flatMap Observable.just(true)
                                    }else{
                                        return@flatMap ZQManager.exitMeeting()  //回滚退出会议
                                            .map {
                                                return@map false
                                            }
                                    }
                                }
                        }
                } else {
                    return@flatMap Observable.just(false)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                isLoading.postValue(OneTimeEvent(false))
                ToastUtils.showShort(getResources().getString(R.string.create_meeting_success))
                goMeeting.postValue(OneTimeEvent(true))
                //创建成功
            }, {
                isLoading.postValue(OneTimeEvent(false))
                ToastUtils.showShort(getResources().getString(R.string.create_meeting_failed))
                logPrint2File(it,"CreateMeetingViewModel#createMeeting")
            })
        )

    }


}