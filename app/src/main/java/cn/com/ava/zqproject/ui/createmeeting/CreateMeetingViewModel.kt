package cn.com.ava.zqproject.ui.createmeeting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.util.GsonUtil
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.manager.ZQManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.ContractGroup
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.DefaultLayoutInfo
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

    private var meetingNo:String=""


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
        //1.??????????????????????????????
        val allInSelected = userIdList.all {
            selectedUsers?.contains(it) ?: false
        }
        if (allInSelected) {   //????????????
            userIdList?.forEach {
                selectedUsers?.remove(it)
            }
            selectedUser.value = selectedUsers
            return true
        } else {   //?????????????????????
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
            } else {  //?????????
                return false
            }

        }

    }

    /**
     * ????????????
     * @param theme ????????????
     * @param nickname ????????????
     * @param waiting ?????????????????????
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
                    ZQManager.loadMeetingInfo()   //??????????????????
                        .flatMap {
                            meetingNo = it.confId
                            return@flatMap PlatformApi.getService().createMeeting(  //????????????
                                initiator = nickname,
                                meetingTitle = theme,
                                meetingNo = it.confId,
                                userId = users
                            )
                                .compose(PlatformApi.applySchedulers())
                                .flatMap {
                                    if(it.success){  //??????
                                        return@flatMap Observable.just(true)
                                    }else{
                                        return@flatMap ZQManager.exitMeeting()  //??????????????????
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
                //????????????,????????????????????????
                val selectedUsers = selectedUser.value?: emptyList()
                val defaultLayoutInfo = DefaultLayoutInfo(meetingNo, false,true, selectedUsers)
                val toJson = GsonUtil.toJson(defaultLayoutInfo)
                CommonPreference.putElement(CommonPreference.KEY_DEFAULT_LAYOUT_INFO,toJson)
            }, {
                isLoading.postValue(OneTimeEvent(false))
                ToastUtils.showShort(getResources().getString(R.string.create_meeting_failed))
                logPrint2File(it,"CreateMeetingViewModel#createMeeting")
            })
        )

    }


}