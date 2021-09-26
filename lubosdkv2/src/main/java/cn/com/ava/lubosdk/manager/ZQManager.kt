package cn.com.ava.lubosdk.manager

import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.zq.control.*
import cn.com.ava.lubosdk.zq.entity.MeetingInfoZQ
import cn.com.ava.lubosdk.zq.entity.MeetingStateInfoZQ
import cn.com.ava.lubosdk.zq.query.MeetingInfoQuery
import cn.com.ava.lubosdk.zq.query.MeetingMemberQuery
import cn.com.ava.lubosdk.zq.query.MeetingStateInfoQuery
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
     * @param users 用户名数组
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


    /**
     * 加载会议成员
     * */
    fun loadMeetingMember():Observable<List<LinkedUser>>{
        return Observable.create { emitter ->
            runBlocking {
                val result = suspendCoroutine<ListWrapper<LinkedUser>> {
                    AVAHttpEngine.requestSPQuery(
                        MeetingMemberQuery(
                            onResult = { result -> it.resumeWith(Result.success(result as ListWrapper<LinkedUser>)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(result.datas)
            }
        }
    }


    /**
     * 会议特殊状态
     * */
    fun loadMeetingState():Observable<MeetingStateInfoZQ>{
        return Observable.create { emitter ->
            runBlocking {
                val result = suspendCoroutine<MeetingStateInfoZQ> {
                    AVAHttpEngine.requestSPQuery(
                        MeetingStateInfoQuery(
                            onResult = { result -> it.resumeWith(Result.success(result as MeetingStateInfoZQ)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(result)
            }
        }
    }


    /**
     * 设置输出布局
     *
     * @param streamCount 新的布局窗口数
     * @param preLayout   以前的布局（内容为互动成员号)
     */
    fun setVideoLayout(
        streamCount: Int,
        preLayout: List<Int>
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SetMeetingVideoLayoutControlZQControl(
                            streamCount,
                            preLayout,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }
    /**
     * 锁定会议
     * */
    fun lockMeeting(isLock:Boolean):Observable<Boolean>{
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        LockConferenceControl(
                            isLock,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }

    /**
     * 关闭本地输出视频
     * */
    fun setLocalCam(isClose:Boolean):Observable<Boolean>{
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        LocalCamControl(
                            isClose,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }

    /**
     * 设置本地输出音频
     * */
    fun setLocalAudio(isClose: Boolean):Observable<Boolean>{
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        LocalAudioControl(
                            isClose,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }
    /**
     * 设置远程摄像头输出
     * */
    fun setRemoteCam(num:Int,isClose: Boolean):Observable<Boolean>{
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        RemoteCamControl(
                            isClose,
                            num.toString(),
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 设置远程音频输出
     * */
    fun setRemoteAudio(num:Int,isClose: Boolean):Observable<Boolean>{
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        RemoteAudioControl(
                            isClose,
                            num.toString(),
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }




    /**
     * 设置发言模式
     * */
    fun setRequestSpeakMode(numberId:Int):Observable<Boolean>{
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        SetRequestSpeakModeControl(
                            numberId.toString(),
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    fun setRequestSpeakRet(numberId: Int,agree:Boolean):Observable<Boolean>{
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                        AgreeSpeakerControl(
                            numberId.toString(),
                            agree,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }



    






}

