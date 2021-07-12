package cn.com.ava.lubosdk.manager

import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.NetResult
import cn.com.ava.lubosdk.control.*
import cn.com.ava.lubosdk.entity.*
import cn.com.ava.lubosdk.query.*
import cn.com.ava.lubosdk.spquery.InteracMemberInfoQuery
import cn.com.ava.lubosdk.spquery.LastCallUserQuery
import cn.com.ava.lubosdk.spquery.RecentCallUserQuery
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object InteracManager {

    const val PAGE_SIZE = 12

    /**
     * 互动云设置
     *
     * @param netparam
     * @param protocol
     * @param code
     * @param enableCloud 内置云是否开启
     * @param asswitch
     */
    fun setInteracCloudSetting(
            netparam: String, protocol: String, code: String,
            asswitch: String,
            enableCloud: Boolean
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            SetInteracSettingControl(
                                    netparam, protocol, code, asswitch, enableCloud,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 获取互动云设置
     */
    fun getInteracSetting(): Observable<InteracSetting> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<InteracSetting> {
                    AVAHttpEngine.addQueryCommand(InteracSettingQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as InteracSetting))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 上次互动设置
     * */
    open fun getLastInteracSetting(): Observable<LastInteracSetting> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<LastInteracSetting> {
                    AVAHttpEngine.addQueryCommand(LastInteracSettingQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as LastInteracSetting))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }

    /**
     * 会议基础信息
     */
    fun getMeetingInfo(): Observable<MeetingInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<MeetingInfo> {
                    AVAHttpEngine.addQueryCommand(MeetingInfoQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as MeetingInfo))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }

    /**
     * 获得选择源 hdmi
     */
    fun getHDMISourceSelect(): Observable<TsInteracSource> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<TsInteracSource> {
                    AVAHttpEngine.addQueryCommand(HDMISourceSelectQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as TsInteracSource))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    fun shareDocSIPH323(isShare: Boolean): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            ShareDocSIPH323Control(
                                    isShare,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 设置主流
     */
    fun postMainStream(videoIndex: Int): Observable<Boolean> {
        return setCurStream(true, LocalVideoStream().apply {
            windowIndex = videoIndex
        })

    }


    /**
     * 设置辅流
     */
    fun postSubStream(videoIndex: Int): Observable<Boolean> {
        return setCurStream(false, LocalVideoStream().apply {
            windowIndex = videoIndex
        })
    }


    /**
     * 通讯录单页
     *
     * @param pageIndex 页码
     * @param limit     每页限制
     */
    fun getMeetingUser(
            pageIndex: Int,
            limit: Int
    ): Observable<Pager<MeetingUser>> {
        val params: MutableMap<String, String> =
                LinkedHashMap()
        params["key"] = LoginManager.getLogin()?.key ?: ""
        params["limit"] = limit.toString() + ""
        params["pageIndex"] = "" + pageIndex
        return Observable.create {
            val call = AVAHttpEngine.getHttpService().getRemoteAddress(params)
            val response = call.execute()
            val pager = response.body()
            if ("0" != pager?.errorCode || pager?.list?.size ?: 0 == 0) {
                it.onError(Exception("Get remote Userbook failed."))
            } else it.onNext(pager)
        }
    }


    /**
     * 获取所有通讯录
     */
    fun getAllMeetingUser(): Observable<Pager<MeetingUser>> {
        return getMeetingUser(1, 1)
                .flatMap { pager ->
                    val total = pager.total.toInt()
                    var pageCount: Int = total / PAGE_SIZE
                    if (total % InteracManager.PAGE_SIZE != 0) pageCount++
                    return@flatMap Observable.range(1, pageCount)
                }.flatMap { page ->
                    return@flatMap getMeetingUser(page, InteracManager.PAGE_SIZE)
                }
    }


    /**
     * 创建会议
     *
     * @param theme      会议主题
     * @param password   会议密码
     * @param codeMode   会议编码H264/H265
     * @param cofType    会议模式 AXM 是授课模式 / NORMAL_CLOUD 是会议模式 , 注意 ,选择用户人数大于 3 时 ,只能 创建 会议模式
     * @param streamMode 开启双流模式 , DUAL 表示双流模式 , SINGLE 表示 单流模式
     * @param maxShow    最大连接数,内置云1拖1为2 ，内置云1拖3为4
     * @param users      连接用户数组（短号、用户名、内置云格式)
     */
    fun createMeeting(
            theme: String, password: String, codeMode: String,
            cofType: String, streamMode: String, maxShow: Int,
            users: Array<String>,
            isInternalCloud: Boolean
    ): Observable<Boolean> {
        val cofTypeAdapt = when (cofType) {
            "AXM" -> "teachMode"
            else -> "confMode"
        }
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            NewCreateMeetingControl(
                                    theme, password,
                                    codeMode, cofTypeAdapt,
                                    streamMode, isInternalCloud, users,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 询问是否可以创建会议
     */
    fun canCreateMeeting(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<SimpleWrapper<Boolean>> {
                    AVAHttpEngine.addQueryCommand(CanCreateMeetingQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as SimpleWrapper<Boolean>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.value)
            }
        }
    }


    /**
     * 获取最近呼叫
     */
    fun getRecentCallUser(): Observable<List<LastCallResult.LastCallUser>> {
        return Observable.create { emitter ->
            runBlocking {
                val result = suspendCoroutine<ListWrapper<LastCallResult.LastCallUser>> {
                    AVAHttpEngine.requestSPQuery(RecentCallUserQuery(
                            onResult = { queryResult -> it.resumeWith(Result.success(queryResult as ListWrapper<LastCallResult.LastCallUser>)) },
                            onError = { throwable -> it.resumeWithException(throwable) }
                    ))
                }
                emitter.onNext(result.datas)
            }

        }
    }


    /**
     * 获取上次呼叫
     */
    fun getLastCallUser(): Observable<List<LastCallResult.LastCallUser>> {
        return Observable.create { emitter ->
            runBlocking {
                val result = suspendCoroutine<ListWrapper<LastCallResult.LastCallUser>> {
                    AVAHttpEngine.requestSPQuery(LastCallUserQuery(
                            onResult = { queryResult -> it.resumeWith(Result.success(queryResult as ListWrapper<LastCallResult.LastCallUser>)) },
                            onError = { throwable -> it.resumeWithException(throwable) }
                    ))
                }
                emitter.onNext(result.datas)
            }

        }
    }


    /**
     * 最近呼叫和上次呼叫结合
     */
    fun getRecentAndLastCallUser(): Observable<List<LastCallResult.LastCallUser>> {
        return Observable.zip<List<LastCallResult.LastCallUser>, List<LastCallResult.LastCallUser>, List<LastCallResult.LastCallUser>>(
                getLastCallUser().subscribeOn(Schedulers.io()),
                getRecentCallUser().subscribeOn(Schedulers.io()),
                BiFunction<List<LastCallResult.LastCallUser>, List<LastCallResult.LastCallUser>, List<LastCallResult.LastCallUser>> { lastCallUsers, lastCallUsers2 ->
                    for (lastCallUser in lastCallUsers) {
                        if (lastCallUsers2.contains(lastCallUser)) {
                            val i = lastCallUsers2.indexOf(lastCallUser)
                            lastCallUsers2[i].isLastCall = true
                        }
                    }
                    lastCallUsers2
                }).doOnNext { list: List<LastCallResult.LastCallUser> ->
            Collections.sort<LastCallResult.LastCallUser>(
                    list
            ) { o1: LastCallResult.LastCallUser, o2: LastCallResult.LastCallUser ->
                if (o1.isSelected() && !o2.isSelected()) {
                    return@sort -1
                } else if (o2.isSelected() && !o1.isSelected()) {
                    return@sort 1
                } else {
                    var lastCallTime1: String = o1.getLastCallTime()
                    var lastCallTime2: String = o2.getLastCallTime()
                    if (!lastCallTime1.contains(" ")) {
                        lastCallTime1 = "$lastCallTime1 00:00:00"
                    }
                    if (!lastCallTime2.contains(" ")) {
                        lastCallTime2 = "$lastCallTime2 00:00:00"
                    }
                    val format: DateFormat =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    var parse1: Date? = null
                    var parse2: Date? = null
                    try {
                        parse1 = format.parse(lastCallTime1)
                        parse2 = format.parse(lastCallTime2)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    return@sort parse2!!.compareTo(parse1)
                }
            }
        }
    }


    /**
     * 查看是否内置云模式
     *
     * @return true内置云模式 false非内置云模式
     */
    fun checkIsInnerCloud(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<SimpleWrapper<Boolean>> {
                    AVAHttpEngine.addQueryCommand(InnerCloudQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as SimpleWrapper<Boolean>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.value)
            }
        }
    }


    /**
     * 询问加入狐疑需不需要编码
     */
    fun isEncodeJoinPsw(): Observable<Boolean?>? {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<SimpleWrapper<Boolean>> {
                    AVAHttpEngine.addQueryCommand(IsEncodeJoinMeetingPswQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as SimpleWrapper<Boolean>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.value)
            }
        }
    }


    /**
     * 加入会议
     *
     * @param meetingId
     * @param password  新版本需要编码
     * @param type      AVA/SIP/H323  AVA密码长度必须为6位  SIP?H323会议密码只能输入 0-9 A-D *#
     * @param mode      TCP/UDP   除了SIP，其他只能选TCP
     */
    fun joinMeeting(meetingId: String, password: String,
                    type: String, mode: String): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val result = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(JoinMeetingControl(meetingId, password, type, mode,
                            onResult = { b -> it.resumeWith(Result.success(b)) },
                            onError = { throwable -> it.resumeWithException(throwable) }),
                    isEncode = true)

                }
                emitter.onNext(result)
            }

        }
    }

    /********************************授课主讲模式**************************************/
    /**
     * 授课主讲信息
     */
    fun getInteracTeacherModeInfo(): Observable<InteracTeacherModeInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<InteracTeacherModeInfo> {
                    AVAHttpEngine.addQueryCommand(InteracTeacherModeInfoQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as InteracTeacherModeInfo))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 获取互动成员信息
     */
    fun getInteracMemberInfo(): Observable<InteraInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val result = suspendCoroutine<InteraInfo> {
                    AVAHttpEngine.requestSPQuery(InteracMemberInfoQuery(
                            onResult = { queryResult -> it.resumeWith(Result.success(it as InteraInfo)) },
                            onError = { throwable -> it.resumeWithException(throwable) }
                    ))
                }
                emitter.onNext(result)
            }

        }
    }


    /**
     * 重拨用户
     */
    fun redialUser(user: LinkedUser): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            RedialUserControl(
                                    user.username,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 退出互动
     */
    fun exitInteraction(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            NewExitInteractionControl(
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 一键重连
     * */
    fun oneKeyReconnect(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            OneKeyReconnectControl(
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 获取互动视频源列表
     */
    fun getInteracVideoList(): Observable<List<InteracVideoSource>> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<ListWrapper<InteracVideoSource>> {
                    AVAHttpEngine.addQueryCommand(InteracVideoSourcesQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as ListWrapper<InteracVideoSource>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.datas)
            }
        }
    }


    /**
     * 是否sip跨域互动
     */
    fun isSipInteraction(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<SimpleWrapper<Boolean>> {
                    AVAHttpEngine.addQueryCommand(IsSipInteractionQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as SimpleWrapper<Boolean>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.value)
            }
        }
    }


    /**
     * 获得选择源(教师或者学生互动视频源）
     */
    fun getSourceSelect(): Observable<List<TsInteracSource>> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<ListWrapper<TsInteracSource>> {
                    AVAHttpEngine.addQueryCommand(TsInteracSourceQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as ListWrapper<TsInteracSource>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.datas)
            }
        }

    }


    /**
     * 选择互动视频源
     */
    fun selectTSSource(
            tSelect: Int,
            sSelect: Int
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            TeachModeVideoSelectControl(
                                    tSelect,
                                    sSelect,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 选择互动视频源
     */
    fun selectHDMISource(hdmisource: Int): Observable<Boolean?>? {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            SelectHDMISourceControl(
                                    hdmisource,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) }
                            )
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 设置互动预览窗口
     *
     * @param windows 最多4个，互动视频源的序号
     */
    fun setVideoWindow(windows: List<Int>): Observable<Boolean> {
        if (windows.size > 4) {
            return Observable.empty()
        }
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            TeachModeLayoutControl(
                                    windows,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /*********************************会议主讲模式******************************************/


    /*********************************会议主讲模式 */
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
                            SetMeetingVideoLayoutControl(
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
     * 会议主讲的与会画面
     *
     * @param isMain 主流输出/辅流输出
     * @param stream 流
     */
    fun setCurStream(
            isMain: Boolean,
            stream: LocalVideoStream
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            if (isMain) {
                                MainStreamSelectControl(
                                        stream.windowIndex,
                                        onResult = { b -> it.resumeWith(Result.success(b)) },
                                        onError = { throwable -> it.resumeWithException(throwable) })
                            } else {
                                SubStreamSelectControl(
                                        stream.windowIndex,
                                        onResult = { b -> it.resumeWith(Result.success(b)) },
                                        onError = { throwable -> it.resumeWithException(throwable) })

                            }
                    )
                }
                emitter.onNext(isSuccess)
            }
        }

    }


    /**
     * 拉取画面流信息
     *
     * @param isMain true主流  false 辅流
     */
    fun getSceneStream(isMain: Boolean): Observable<List<LocalVideoStream>> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<ListWrapper<LocalVideoStream>> {
                    AVAHttpEngine.addQueryCommand(LocalVideoStreamQuery(
                            isMain,
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as ListWrapper<LocalVideoStream>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.datas)
            }
        }
    }


    /**
     * 获取会议成员信息
     */
    fun getInteracInfo(): Observable<InteraInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<InteraInfo> {
                    AVAHttpEngine.requestSPQuery(InteracMemberInfoQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as InteraInfo))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    /***********************************听课模式********************************************/

    /***********************************听课模式 */
    /**
     * 申请发言
     */
    fun applySpeak(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            ApplySpeakControl(
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 分享双流
     */
    fun shareDoc(): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            ShareDocControl(
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 获取我的互动名
     */
    fun getMyName(): Observable<String> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<SimpleWrapper<String>> {
                    AVAHttpEngine.addQueryCommand(MyInteractionNameQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as SimpleWrapper<String>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.value)
            }
        }
    }


    /**
     * 获取我的互动编号(听课无法知道其他参与者)/旧版本通过
     */
    fun getMyInteractionNumber(): Observable<Int> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<SimpleWrapper<Int>> {
                    AVAHttpEngine.addQueryCommand(MyInteractionNumberQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as SimpleWrapper<Int>))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info.value)
            }
        }
    }


    /**
     * 获取听课所需信息(接受轮询的信息)
     */
    fun getListenerInfo(): Observable<ListenerInfo> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<ListenerInfo> {
                    AVAHttpEngine.addQueryCommand(ListenerInfoQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as ListenerInfo))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * 新版一键呼叫接口
     */
    fun oneKeyCallNew(
            username: String,
            password: String
    ): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            OneKeyCallControl(
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 用户断开会议,连续两次调用踢出本会议通讯录
     */
    fun disconnectUser(username: String): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            DisconnectUserControl(
                                    username,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }


    /**
     * 从会议中踢出用户
     */
    fun kickOutUser(username: String): Observable<Boolean> {
        return Observable.timer(2000, TimeUnit.MILLISECONDS)
                .flatMap { disconnectUser(username) }
    }


    /**海南特供部分*/


    /*********************************海南特供部分********************************* */
    /**
     * 主讲互动下控制远程视频窗口
     *
     * @param rid 远程号
     * @param vid 视频号
     */
    fun controlRemoteVideo(rid: Int, vid: Int): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            RemoteVideoControl(
                                    rid, vid,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }
    }

    /**
     * 获取远程视频使能
     */
    fun getRemoteVideoControlEnable(): Observable<RemoteVideoEnable> {
        return Observable.create { emitter ->
            runBlocking {
                val info = suspendCoroutine<RemoteVideoEnable> {
                    AVAHttpEngine.addQueryCommand(RemoteVideoEnableQuery(
                            onResult = { queryResult ->
                                it.resumeWith(Result.success(queryResult as RemoteVideoEnable))
                            },
                            onError = { throwable ->
                                it.resumeWithException(throwable)
                            }
                    ))
                }
                emitter.onNext(info)
            }
        }
    }


    /**
     * sip互动推送
     */
    fun sipPush(sip: String, courseId: String, csTitle: String, startTime: String,
                endTime: String, csTeacher: String, isTeacher: Boolean,
                ridCount: Int, rids: List<String>, rNames: List<String>): Observable<NetResult> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            SipPushControl(
                                    sip, courseId, csTitle, startTime, endTime, csTeacher, isTeacher, ridCount, rids, rNames,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(NetResult(if (isSuccess) 0 else -1, if (isSuccess) "success" else "failed"))
            }
        }
    }


    /**
     * KP8P面板新指令
     */
    fun setNewKp8PKey(key: Int): Observable<Boolean> {
        return Observable.create { emitter ->
            runBlocking {
                val isSuccess = suspendCoroutine<Boolean> {
                    AVAHttpEngine.requestControl(
                            KP8PControl(
                                    key,
                                    onResult = { b -> it.resumeWith(Result.success(b)) },
                                    onError = { throwable -> it.resumeWithException(throwable) })
                    )
                }
                emitter.onNext(isSuccess)
            }
        }

    }


}