package cn.com.ava.lubosdk.manager

import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.zq.control.*
import cn.com.ava.lubosdk.zq.entity.MeetingInfoZQ
import cn.com.ava.lubosdk.zq.query.MeetingInfoQuery
import io.reactivex.Observable
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ZQManager {


    /**
     * 创建会议
     * @param theme 主题
     * @param password 密码
     * @param nickname 入会昵称
     * @param isWaitingRoom 是否开启等候室
     * @param users 会议成员
     * */
    fun createMeeting(
        theme: String,
        password: String,
        nickname: String,
        isWaitingRoom: Boolean,
        users: Array<String>
    ): Observable<Boolean> {
        return Observable.create<Boolean> { emitter ->
            runBlocking {
                val setNickname = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SetMeetingNicknameControl(nickname,
                            onResult = { result -> it.resumeWith(Result.success(result)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                if (setNickname) { // 成功
                    val createMeeting = suspendCoroutine<Boolean> {
                        AVAHttpEngine.requestControl(
                            CreateMeetingZQControl(theme, password, isWaitingRoom, users,
                                onResult = { result -> it.resumeWith(Result.success(result)) },
                                onError = { throwable -> it.resumeWithException(throwable) })
                        )
                    }
                    emitter.onNext(createMeeting)
                } else {   //失败
                    emitter.onNext(false)
                }
            }
        }
    }


    /**
     * 加入会议
     * @param confId 会议号
     * @param password 密码
     * @param confAc 一次性密码
     * @param nickname 入会昵称
     * */
    fun joinMeeting(
        confId: String,
        password: String,
        confAc: String,
        nickname: String
    ): Observable<Boolean> {
        return Observable.create<Boolean> { emitter ->
            runBlocking {
                val setNickname = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SetMeetingNicknameControl(nickname,
                            onResult = { result -> it.resumeWith(Result.success(result)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                if (setNickname) { // 成功
                    val createMeeting = suspendCoroutine<Boolean> {
                        AVAHttpEngine.requestControl(
                            JoinMeetingZQControl(confId, password, confAc,
                                onResult = { result -> it.resumeWith(Result.success(result)) },
                                onError = { throwable -> it.resumeWithException(throwable) })
                        )
                    }
                    emitter.onNext(createMeeting)
                } else {   //失败
                    emitter.onNext(false)
                }
            }
        }
    }


    /**
     * 退出会议
     * */
    fun exitMeeting(): Observable<Boolean> {
        return Observable.create<Boolean> { emitter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        ExitMeetingZQControl(
                            onResult = { result -> it.resumeWith(Result.success(result)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(result)
            }
        }
    }



    /**
     * 听课/旁听角色切换
     * @param users 用户
     * @param goToWaiting 去等候室
     * */
    fun memberGoToWaiting(users:Array<String>,goToWaiting:Boolean):Observable<Boolean>{
        return Observable.create<Boolean> { emitter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        MeetingMemberWaitingControl(users,if(goToWaiting)"viewer" else "listener",
                            onResult = { result -> it.resumeWith(Result.success(result)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(result)
            }
        }
    }

    /**
     * 加载会议信息
     * */
    fun loadMeetingInfo():Observable<MeetingInfoZQ>{
        return Observable.create { emitter ->
            runBlocking {
                val result = suspendCoroutine<MeetingInfoZQ> {
                    AVAHttpEngine.requestSPQuery(
                        MeetingInfoQuery(
                            onResult = { result -> it.resumeWith(Result.success(result as MeetingInfoZQ)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(result)
            }
        }
    }


    






}

