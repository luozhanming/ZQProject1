package cn.com.ava.zqproject.ui.meeting

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.DateUtil
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.entity.*
import cn.com.ava.lubosdk.entity.zq.ApplySpeakUser
import cn.com.ava.lubosdk.manager.*
import cn.com.ava.lubosdk.zq.entity.MeetingInfoZQ
import cn.com.ava.lubosdk.zq.entity.MeetingStateInfoZQ
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.common.RecordUploadManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.InteractComputerSource
import cn.com.ava.zqproject.vo.InteractComputerSources
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class MasterViewModel : BaseViewModel() {

    /**
     * 电脑信息
     * */
    val computerSources: MutableLiveData<InteractComputerSources> by lazy {
        MutableLiveData()
    }

    /**
     * 当前互动视频源
     * */
    val curSceneSources: MutableLiveData<List<LocalVideoStream>> by lazy {
        MutableLiveData()
    }

    private var mLoopMeetingInfoDisposable: Disposable? = null

    private var mLoopCurSceneSourcesDisposable: Disposable? = null

    private var mLoopInteracInfoDisposable: Disposable? = null

    private var mLoopMeetingInfoZQDisposable: Disposable? = null

    private var mLoopMeetingStateZQDisposable: Disposable? = null

    private var mApplySpeakListenLoopDisposable: Disposable? = null

    private var mPatrolManager: PatrolManager? = null

    private var isModifyTheme:Boolean = false


    val isShowLoading: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    /**
     * 会议信息
     * */
    val meetingInfo: MutableLiveData<MeetingInfo> by lazy {
        MutableLiveData()
    }

    /**
     * 控制栏隐藏
     * */
    val isControlVisible: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = true
        }
    }


    /**
     * 电脑索引
     * */
    private val computerIndex = ComputerModeManager.computerIndex().replace("V", "").toInt()

    /**
     * 是否接入电脑
     * */
    val isPluginComputer: MediatorLiveData<Boolean> by lazy {
        MediatorLiveData<Boolean>().apply {
            addSource(curSceneSources) {
                val computerStream = it.firstOrNull { it.windowIndex == computerIndex }
                if (computerStream?.isMainOutput != value) {
                    postValue(computerStream?.isMainOutput ?: false)
                }
                val curOutput = it.firstOrNull { stream -> stream.isMainOutput }
                if (computerIndex != curOutput?.windowIndex) {
                    if (lastNonComputerScene != curOutput) {
                        lastNonComputerScene = curOutput
                    }
                } else {
                    if (lastNonComputerScene == null) {
                        lastNonComputerScene = it[0]
                    }
                }

            }
        }
    }

    /**
     * 退出会议
     * */
    val exitMeeting: MediatorLiveData<Boolean> by lazy {
        MediatorLiveData<Boolean>().apply {
            addSource(meetingInfoZq) {
                val value = "created" != meetingInfoZq.value?.confStatus
                if (value) {
                    postValue(true)
                }
            }
        }
    }

    /**
     * 电脑视频源列表
     * */
    val computerSourceList: MutableLiveData<List<InteractComputerSource>> by lazy {
        MutableLiveData()
    }

    /**
     * 录制状态
     * */
    val isRecording: MediatorLiveData<Boolean> by lazy {
        MediatorLiveData<Boolean>().apply {
            addSource(meetingInfo) { info ->
                val isRecording = value ?: false
                val newRecording = info.recordState == Constant.RECORD_RECORDING
                if (isRecording != newRecording) {
                    value = newRecording
                }

            }
        }
    }

    /**
     * 弹出录制结束归档对话框
     * */
    val showUpload: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    /**
     * 互动信息
     * */
    val interacInfo: MutableLiveData<InteraInfo> by lazy {
        MutableLiveData()
    }

    /**
     * 画面上的用户
     * */
    val onVideoWindow: MediatorLiveData<List<LinkedUser>> by lazy {
        MediatorLiveData<List<LinkedUser>>().apply {
            addSource(interacInfo) {
                val layout = it.layout
                layout.removeAt(0)
                val onlineList = it.onlineList ?: return@addSource
                val onVideoWindowList = onlineList.filter {
                    it.number in layout
                }
                onVideoWindowList.forEach {
                    it.isOnVideo = true
                }
                val windowOnVideo = arrayListOf<LinkedUser>()
                for (i in layout.indices) {
                    var userOnVideo: LinkedUser? = null
                    userOnVideo = onVideoWindowList.firstOrNull { it.number == layout[i] }
                    if (userOnVideo == null) {
                        userOnVideo = LinkedUser()
                        userOnVideo.number = -1
                        windowOnVideo.add(userOnVideo)
                    } else {
                        windowOnVideo.add(userOnVideo)
                    }
                }
                //只有不同的时候才更新
                var hasChanged = false
                if (windowOnVideo.size == value?.size ?: 0) {
                    for (i in windowOnVideo.indices) {
                        if (windowOnVideo[i] != value?.get(i)) {
                            hasChanged = true
                            break
                        }
                    }
                } else {
                    hasChanged = true
                }
                if (hasChanged) {
                    postValue(windowOnVideo)
                }
            }
        }
    }

    /**
     * 画面布局数
     * */
    val videoLayoutCount: MediatorLiveData<Int> by lazy {
        MediatorLiveData<Int>().apply {
            addSource(interacInfo) {
                val layout = it.layout
                if (layout.size > 0) {
                    if (layout[0] != value) {
                        postValue(layout[0])
                    }
                }
            }
        }
    }


    /**
     * 申请发言用户
     * */
    val applySpeakUsers: MutableLiveData<List<ApplySpeakUser>> by lazy {
        MutableLiveData()
    }


    /**
     * 会议信息
     * */
    val meetingInfoZq: MutableLiveData<MeetingInfoZQ> by lazy {
        MutableLiveData()
    }

    /**
     * 会议成员
     * */
    val linkUsers: MutableLiveData<List<LinkedUser>> by lazy {
        MutableLiveData()
    }

    /**
     * 会议时间
     **/
    val meetingTime: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    /**
     * 会议状态
     * */
    val meetingState: MutableLiveData<MeetingStateInfoZQ> by lazy {
        MutableLiveData()
    }

    /**
     * 申请发言模式状态
     * */
    val requestSpeakRet: MediatorLiveData<Int> by lazy {
        MediatorLiveData<Int>().apply {
            value = 0
            addSource(meetingState) {
                it.apply {
                    if (this.requestSpeakMode != value) {
                        if (requestSpeakMode > 0 && requestSpeakRet.value == 0) {   //申请发言前处理一下保存当前画面
                            preRequestSpeakLayout = onVideoWindow.value
                        }
                        postValue(this.requestSpeakMode)
                    }
                }
            }
        }
    }


    var preRequestSpeakLayout: List<LinkedUser>? = null
        get() = field


    var mLoopLinkUserDisposable: Disposable? = null

    private var mTimeCountDisposable: Disposable? = null


    /**
     * 上次不是电脑的画面，用于恢复
     * */
    private var lastNonComputerScene: LocalVideoStream? = null


    fun startLoadMeetingInfo() {
        isShowLoading.value = OneTimeEvent(true)
        mLoopMeetingInfoDisposable?.dispose()
        mLoopMeetingInfoDisposable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
            .flatMap {
                InteracManager.getMeetingInfo()
                    .subscribeOn(Schedulers.io())
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribe({
                if (it.recordState == Constant.RECORD_STOP && meetingInfo.value?.recordState != Constant.RECORD_STOP && meetingInfo.value != null) {
                    loadLatestRecordFile()
                }
                isShowLoading.postValue(OneTimeEvent(false))
                meetingInfo.postValue(it)

            }, {
                isShowLoading.postValue(OneTimeEvent(false))
                logPrint2File(it,"MasterViewModel#startLoadMeetingInfo")
            })
    }

    /**
     * 添加最新的录制文件到上传队列
     * */
    private fun loadLatestRecordFile() {
      showUpload.postValue(OneTimeEvent(true))
//        isShowLoading.postValue(OneTimeEvent(true))
//        mDisposables.add(VideoResourceManager.getRecordFileList()
//            .map {
//                Collections.sort(it) { t1, t2 ->
//
//                    return@sort (t2.recordRawBeginTime
//                        .toLong() - t1.recordRawBeginTime.toLong()).toInt()
//                }
//                it
//            }
//            .subscribeOn(Schedulers.io())
//            .subscribe({
//                isShowLoading.postValue(OneTimeEvent(false))
//                if (it.isNotEmpty()) {
//                    showUpload.postValue(OneTimeEvent(it[0]))
//                }
//            }, {
//                isShowLoading.postValue(OneTimeEvent(false))
//                logPrint2File(it)
//            })
//        )
    }

    fun startLoopInteracInfo() {
        mLoopInteracInfoDisposable?.dispose()
        mLoopInteracInfoDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                InteracManager.getInteracInfo()
                    .subscribeOn(Schedulers.io())
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribe({
                interacInfo.postValue(it)
            }, {
                logPrint2File(it,"MasterViewModel#startLoopInteracInfo")
            })
    }


    fun startLoopMeetingInfoZQ() {
        mLoopMeetingInfoZQDisposable?.dispose()
        mLoopMeetingInfoZQDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                ZQManager.loadMeetingInfo()
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                meetingInfoZq.postValue(it)
                if(!isModifyTheme){
                    modifyTheme()
                }
            }, {
                logPrint2File(it,"MasterViewModel#startLoopMeetingInfoZQ")
            })
    }

    private fun modifyTheme() {
        meetingInfoZq.value?.apply {
            mDisposables.add(
                RecordManager.setClassInfo(confTheme, PlatformApi.getPlatformLogin()?.name?:"")
                    .subscribeOn(Schedulers.io())
                    .subscribe({

                    },{
                        logPrint2File(it,"MasterViewModel#modifyTheme")
                    })
            )

        }

    }

    fun startLoopLinkUsers() {
        mLoopLinkUserDisposable?.dispose()
        mLoopLinkUserDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                ZQManager.loadMeetingMember()
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                linkUsers.postValue(it.datas)
            }, {
                logPrint2File(it,"MasterViewModel#startLoopLinkUsers")
            })
    }

    fun startLoopMeetingState() {
        mLoopMeetingStateZQDisposable?.dispose()
        mLoopMeetingStateZQDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                ZQManager.loadMeetingState()
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({

                meetingState.postValue(it)
                //处理申请发言的列表数据源
                val applySpeakUser = arrayListOf<ApplySpeakUser>()
                it.requestSpeakStatus?.apply {
                    forEach { numId ->
                        linkUsers.value?.apply {
                            val findUser = firstOrNull { user ->
                                user.number == numId
                            }
                            if (findUser != null) {
//                                ApplySpeakManager.addApplySpeakUser(
//                                    ApplySpeakUser(
//                                        numId,
//                                        findUser.username,
//                                        "",
//                                        findUser.nickname
//                                    )
//                                )
                                applySpeakUser.add( ApplySpeakUser(
                                        numId,
                                        findUser.username,
                                        "",
                                        findUser.nickname
                                    ))
                            }
                        }
                    }
                }
                applySpeakUsers.postValue(applySpeakUser)
            }, {
                logPrint2File(it,"MasterViewModel#startLoopMeetingState")
            })
    }


    fun toggleComputer() {
        var postIndex = 1
        if (isPluginComputer.value == true) {
            postIndex = lastNonComputerScene?.windowIndex ?: 1
        } else {
            postIndex = computerIndex
        }
        mDisposables.add(
            InteracManager.postMainStream(postIndex)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it,"MasterViewModel#toggleComputer")
                })
        )
    }

    fun overMeeting() {
        mDisposables.add(
            ZQManager.exitMeeting()
                .subscribeOn(Schedulers.io())
                .subscribe({
                }, {
                    logPrint2File(it,"MasterViewModel#overMeeting")
                })
        )
    }


    fun loadApplySpeakUsers() {
//        ApplySpeakManager.addApplySpeakUser(ApplySpeakUser(1, "悟空", "撒亚人"))
//        ApplySpeakManager.addApplySpeakUser(ApplySpeakUser(2, "悟饭", "撒亚人"))
//        ApplySpeakManager.addApplySpeakUser(ApplySpeakUser(3, "悟天", "撒亚人"))
//        ApplySpeakManager.addApplySpeakUser(ApplySpeakUser(4, "比达", "撒亚人"))
    }

    /**
     * 开启申请发言列表监听
     * */
    fun startApplySpeakListen() {
//        mApplySpeakListenLoopDisposable?.dispose()
//        mApplySpeakListenLoopDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
//            .subscribe({
//                val users = ApplySpeakManager.getApplySpeakUsers()
//                applySpeakUsers.postValue(users)
//            }, {
//                logPrint2File(it)
//            })
    }

    /**
     * 停止申请发言列表监听
     * */
    fun stopApplySpeakListen() {
        mApplySpeakListenLoopDisposable?.dispose()
    }

    fun getComputerSourceInfo() {
        mDisposables.add(
            WindowLayoutManager.getPreviewWindowInfo()
                .map {
                    //获取电脑索引
                    val computerIndex = ComputerModeManager.computerIndex().replace("V", "").toInt()
                    if (it.size >= computerIndex) {
                        val computerWindow = it[computerIndex - 1]
                        val computerSource = InteractComputerSources(
                            computerIndex,
                            computerWindow.isHasMultiSource,
                            computerWindow.curSourceIndex,
                            computerWindow.sources?: emptyList(),
                            computerWindow.sourcesCmd?: emptyList()
                        )
                        return@map computerSource
                    } else {
                        return@map null
                    }
                }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it != null) {
                        computerSources.postValue(it)
                    }
                    it?.apply {
                        val sourceList = arrayListOf<InteractComputerSource>()
                        for (i in sources.indices) {
                            if (sources.get(i).contains("HDMI")) {
                                val source = InteractComputerSource(
                                    computerIndex,
                                    isHasMultiSource,
                                    curSourceIndex - 1 == i,
                                    this.sources[i],
                                    sourcesCmd[i]
                                )
                                sourceList.add(source)
                            }
                        }
                        computerSourceList.postValue(sourceList)
                    }
                }, {
                    logPrint2File(it,"MasterViewModel#getComputerSourceInfo")
                })
        )
    }


    fun toggleRecord() {
        val curState = meetingInfo.value?.recordState ?: Constant.RECORD_STOP
        var nextState = when (curState) {
            Constant.RECORD_STOP -> 1
            else ->0
        }
        mDisposables.add(
            ZQManager.setRecordState(nextState)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it,"MasterViewModel#toggleRecord")
                })
        )
    }

    fun toggleLive() {
        mDisposables.add(
            RecordManager.controlLiving(meetingInfo.value?.isLiving?.not() ?: false)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it,"MasterViewModel#toggleLive")
                })
        )
    }


    fun loopCurVideoSceneSources() {
        mLoopCurSceneSourcesDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                InteracManager.getSceneStream(true)
//                    .map {
//                        //过滤掉电脑的选项
//                        it.filter { stream->
//                            stream.windowIndex!=ComputerModeManager.computerIndex().replace("V","").toInt()
//                        }
//                        it
//                    }
                    .subscribeOn(Schedulers.io())
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribe({
                curSceneSources.postValue(it)
            }, {
                logPrint2File(it,"MasterViewModel#loopCurVideoSceneSources")
            })
    }

    override fun onCleared() {
        super.onCleared()
        exitMeeting.removeSource(meetingInfo)
        isPluginComputer.removeSource(curSceneSources)
        isRecording.removeSource(meetingInfo)
        onVideoWindow.removeSource(interacInfo)
        videoLayoutCount.removeSource(interacInfo)
        requestSpeakRet.removeSource(meetingState)
        mLoopMeetingInfoDisposable?.dispose()
        mLoopCurSceneSourcesDisposable?.dispose()
        mLoopInteracInfoDisposable?.dispose()
        mApplySpeakListenLoopDisposable?.dispose()
        mLoopMeetingInfoZQDisposable?.dispose()
        mLoopLinkUserDisposable?.dispose()
        mLoopMeetingStateZQDisposable?.dispose()
        mPatrolManager?.cancel()
        mPatrolManager = null
    }


    fun setVideoLayout(usersOnVideo: List<Int>) {
        mDisposables.add(
            ZQManager.setVideoLayout(usersOnVideo.size, usersOnVideo)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it,"MasterViewModel#setVideoLayout")
                })
        )
    }

    /**
     * 开启会议计时
     * */
    fun startTimeCount() {
        mTimeCountDisposable?.dispose()
        mTimeCountDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .subscribe({
                meetingInfoZq.value?.apply {
                    if (!TextUtils.isEmpty(confStartTime)) {
                        val begin = DateUtil.toTimeStamp(confStartTime, "yyyy-MM-dd_HH:mm:ss")
                        val now = System.currentTimeMillis()
                        val diff = now - begin

                        val toDateString = DateUtil.toHourString(diff)
                        meetingTime.postValue(toDateString)
                    }
                }
            }, {
                logPrint2File(it,"MasterViewModel#startTimeCount")
            })
    }

    /**
     * 停止会议计时
     * */
    fun stopTimeCount() {
        mTimeCountDisposable?.dispose()
    }

    /**
     * 开关本地音画
     * */
    fun toggleLocalVolumeAudio() {
        meetingState.value?.apply {
            mDisposables.add(
                Observable.zip(
                    ZQManager.setLocalCam(localCameraCtrl),
                    ZQManager.setLocalAudio(localCameraCtrl),
                    { t1, t2 ->
                        return@zip t1 && t2
                    }).subscribeOn(Schedulers.io())
                    .subscribe({

                    }, {
                        logPrint2File(it,"MasterViewModel#toggleLocalVolumeAudio")
                    })
            )
        }
    }

    /**
     * 设置发言状态
     * */
    fun setRequestSpeakMode(number: Int) {
        mDisposables.add(
            ZQManager.setRequestSpeakMode(number)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it,"MasterViewModel#setRequestSpeakMode")
                })
        )
    }

    /**
     * 是否同意发言
     * */
    fun agreeRequestSpeak(numId: Int, agree: Boolean) {
        mDisposables.add(
            ZQManager.setRequestSpeakRet(numId, agree)
                .flatMap {
                    if (!it) {  //不成功
                        return@flatMap Observable.just(false)
                    } else if (agree) {  //设置发言状态
                        return@flatMap ZQManager.setRequestSpeakMode(numId)
                    } else {
                        return@flatMap ZQManager.setRequestSpeakMode(0)
                    }
                }
//                .flatMap { it1 -> //关闭所有
//                    val requestNum = meetingState.value?.requestSpeakStatus ?: emptyList()
//                    return@flatMap Observable.fromIterable(requestNum)
//                        .flatMap {
//                            if (numId != it) {
//                                return@flatMap ZQManager.setRequestSpeakRet(it, false)
//                            } else {
//                                return@flatMap Observable.just(it1)
//                            }
//                        }
//                }
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it,"MasterViewModel#agreeRequestSpeak")
                })
        )
    }

    /**
     * 切换锁定会议
     * */
    fun toggleLockMeeting() {
        meetingState.value?.apply {
            mDisposables.add(
                ZQManager.lockMeeting(!lockConference)
                    .subscribeOn(Schedulers.io())
                    .subscribe({

                    }, {
                        logPrint2File(it,"MasterViewModel#toggleLockMeeting")
                    })
            )
        }

    }

    fun beginPatrol(selectedUser: List<LinkedUser>?, period: Int) {
        selectedUser?.apply {
            mPatrolManager = mPatrolManager ?: PatrolManager(object : PatrolManager.PatrolCallback {
                override fun onBegin() {

                }

                override fun onCancel() {

                }

                override fun onChanged(user: LinkedUser) {
                    logd("onChanged")
                    setVideoLayout(listOf(user.number))
                }

            })
            mPatrolManager?.begin(selectedUser, period.toLong())
        }
    }

    fun cancelPatrol() {
        mPatrolManager?.cancel()
    }


    class PatrolManager(val callback: PatrolCallback? = null) {

        interface PatrolCallback {

            fun onBegin()
            fun onCancel()
            fun onChanged(user: LinkedUser)
        }


        private var patrolUsers = arrayListOf<LinkedUser>()

        private var timer: Disposable? = null

        private var curPos = 0


        fun begin(users: List<LinkedUser>, period: Long) {
            curPos = 0
            timer?.dispose()
            patrolUsers.clear()
            patrolUsers.addAll(users)
            timer = Observable.interval(0,period, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val size = patrolUsers.size
                    curPos %= size
                    callback?.onChanged(patrolUsers[curPos])
                    curPos++
                }, {
                    logPrint2File(it,"MasterViewModel#begin")
                })
        }

        fun cancel() {
            timer?.dispose()
            callback?.onCancel()
        }


    }


}