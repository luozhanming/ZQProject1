package cn.com.ava.zqproject.ui.meeting

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.lubosdk.manager.ZQManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.vo.MeetingMember
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MemberManagerViewModel : BaseViewModel() {


    private var waitingRoom: Boolean = false

    /**
     * 已入会成员
     * */
    val onMeetingMembers: MediatorLiveData<List<MeetingMember>> by lazy {
        MediatorLiveData<List<MeetingMember>>().apply {
            addSource(meetingMember) {
                val listeners = it.filter { user ->
                    user.role == "1" && user.onlineState == 1
                }
                val list = arrayListOf<MeetingMember>()
                listeners.forEach {
                    val camState = memberCamState.value ?: emptyList()
                    val micState = memberMicState.value ?: emptyList()
                    val member = MeetingMember(it, it.number !in micState, it.number !in camState)
                    list.add(member)
                }
                value = list
            }
        }
    }

    /**
     * 等待室成员
     * */
    val onWaitingMembers: MediatorLiveData<List<LinkedUser>> by lazy {
        MediatorLiveData<List<LinkedUser>>().apply {
            addSource(meetingMember) {
                val listeners = it.filter { user ->
                    //等候室并且在线
                    user.role == "3" && user.onlineState == 1
                }
                value = listeners
            }
        }
    }

    /**
     * 会议成员
     */
    val meetingMember: MutableLiveData<List<LinkedUser>> by lazy {
        MutableLiveData()
    }


    /**
     * 会议成员摄像头状态
     **/
    val memberCamState: MutableLiveData<List<Int>> by lazy {
        MutableLiveData()
    }

    /**
     * 会议成员麦克风状态
     **/
    val memberMicState: MutableLiveData<List<Int>> by lazy {
        MutableLiveData()
    }


    /**
     * 全体是否静音了
     * */
    val isAllSilent: MediatorLiveData<Boolean>  = MediatorLiveData<Boolean>().apply {
            addSource(memberMicState) {
                val onMeetings = onMeetingMembers.value?: emptyList()
                var isAllInMute = false
                if(onMeetings.isNotEmpty()){
                    isAllInMute = onMeetings.all { member->
                        member.user.number in it
                    }
                }
                postValue( isAllInMute)
            }
        }


    private var mLoopAudioParamDisposable: Disposable? = null


    /**
     * 成员关视频
     * */
    fun setMemberCamEnable(numId: Int, enable: Boolean) {
        mDisposables.add(
            ZQManager.setRemoteCam(numId, !enable)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it, "MemberManagerViewModel#setMemberCamEnable")
                    ToastUtils.showShort(getResources().getString(R.string.set_failed))

                })
        )
    }


    /**
     * 成员静音
     * */
    fun setMemberMicEnable(numId: Int, enable: Boolean) {
        mDisposables.add(
            ZQManager.setRemoteAudio(numId, !enable)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it, "MemberManagerViewModel#setMemberMicEnable")
                    ToastUtils.showShort(getResources().getString(R.string.set_failed))

                })
        )
    }

    /**
     * 成员移除
     * */
    fun removeMemberToWaiting(username: String) {
        if (waitingRoom) {
            mDisposables.add(
                ZQManager.memberGoToWaiting(arrayOf(username), true)
                    .subscribeOn(Schedulers.io())
                    .subscribe({

                    }, {
                        logPrint2File(it, "MemberManagerViewModel#removeMemberToWaiting")
                        ToastUtils.showShort(getResources().getString(R.string.set_failed))

                    })
            )
        } else {
            mDisposables.add(
                ZQManager.deleteMeetingMember(username)
                    .subscribeOn(Schedulers.io())
                    .subscribe({

                    }, {
                        logPrint2File(it, "MemberManagerViewModel#removeMemberToWaiting")
                        ToastUtils.showShort(getResources().getString(R.string.set_failed))

                    })
            )
        }

    }

    /**
     * 全体移除
     * */
    fun removeAllMemberToWaiting() {
        val list = onMeetingMembers.value ?: emptyList()
        val numbers = list.map {
            it.user.username
        }
        if (waitingRoom) {
            mDisposables.add(
                ZQManager.memberGoToWaiting(numbers.toTypedArray(), true)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        ToastUtils.showShort(getResources().getString(R.string.set_success))
                    }, {
                        logPrint2File(it, "MemberManagerViewModel#removeAllMemberToWaiting")
                        ToastUtils.showShort(getResources().getString(R.string.set_failed))

                    })
            )
        } else {
            mDisposables.add(
                Observable.fromIterable(numbers)
                    .flatMap {
                        ZQManager.deleteMeetingMember(it)
                    }
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        ToastUtils.showShort(getResources().getString(R.string.set_success))
                    }, {
                        logPrint2File(it, "MemberManagerViewModel#removeAllMemberToWaiting")
                        ToastUtils.showShort(getResources().getString(R.string.set_failed))

                    })
            )
        }

    }

    /**
     * 准入
     * */
    fun acceptMemberToMeeting(username: String) {
        mDisposables.add(
            ZQManager.memberGoToWaiting(arrayOf(username), false)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it, "MemberManagerViewModel#acceptMemberToMeeting")
                    ToastUtils.showShort(getResources().getString(R.string.set_failed))

                })
        )
    }


    /**
     * 全体准入
     * */
    fun acceptAllMemberToMeeting() {
        val list = onWaitingMembers.value ?: emptyList()
        val numbers = list.map {
            it.username.toString()
        }.toTypedArray()
        mDisposables.add(
            ZQManager.memberGoToWaiting(numbers, false)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    ToastUtils.showShort(getResources().getString(R.string.set_success))
                }, {
                    logPrint2File(it, "MemberManagerViewModel#acceptAllMemberToMeeting")
                    ToastUtils.showShort(getResources().getString(R.string.set_failed))
                })
        )
    }

    /**
     * 全体静音
     * */
    fun silentAllMembers() {
        val list = onMeetingMembers.value ?: emptyList()
        val numbers = list.map {
            it.user.number.toString()
        }
        mDisposables.add(Observable.fromIterable(numbers)
            .flatMap {
                ZQManager.setRemoteAudio(it.toInt(), true)
            }.subscribeOn(Schedulers.io())
            .subscribe({

            }, {
                logPrint2File(it, "MemberManagerViewModel#silentAllMembers")
                ToastUtils.showShort(getResources().getString(R.string.set_failed))
            })
        )
    }

    /**
     * 解除全体静音
     * */
    fun cancelAllSilent() {
        val list = onMeetingMembers.value ?: emptyList()
        val numbers = list.map {
            it.user.number.toString()
        }
        mDisposables.add(Observable.fromIterable(numbers)
            .flatMap {
                ZQManager.setRemoteAudio(it.toInt(), false)
            }.subscribeOn(Schedulers.io())
            .subscribe({

            }, {
                logPrint2File(it, "MemberManagerViewModel#silentAllMembers")
                ToastUtils.showShort(getResources().getString(R.string.set_failed))
            })
        )
    }

    fun startLoopMuteMeetingAudio() {
        mLoopAudioParamDisposable?.dispose()
        mLoopAudioParamDisposable = Observable.interval(1000,TimeUnit.MILLISECONDS)
            .flatMap {
                ZQManager.loadMeetingAudioParam()
            }.subscribeOn(Schedulers.io())
            .subscribe({
                isAllSilent.postValue("1"==it.muteStatus)
            },{
                logPrint2File(it,"MemberManagerViewModel#startLoopMuteMeetingAudio")
            })
    }

    fun stopLoopMuteMeetingAudio(){
        mLoopAudioParamDisposable?.dispose()
        mLoopAudioParamDisposable?.dispose()
    }

    fun setWaitingEnable(waitingRoomEnable: Boolean) {
        this.waitingRoom = waitingRoomEnable
    }

    override fun onCleared() {
        super.onCleared()
        isAllSilent.removeSource(memberMicState)
    }


}