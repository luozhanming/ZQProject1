package cn.com.ava.zqproject.ui.meeting

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.entity.InteraInfo
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.lubosdk.entity.LocalVideoStream
import cn.com.ava.lubosdk.entity.MeetingInfo
import cn.com.ava.lubosdk.entity.zq.ApplySpeakUser
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.lubosdk.manager.RecordManager
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.lubosdk.manager.ZQManager
import cn.com.ava.lubosdk.zq.entity.MeetingInfoZQ
import cn.com.ava.zqproject.common.ApplySpeakManager
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.vo.InteractComputerSource
import cn.com.ava.zqproject.vo.InteractComputerSources
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
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

    private var mLoopMeetingInfoZQDisposable:Disposable?=null


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
                val value = "created"!=meetingInfoZq.value?.confStatus
                if(value){
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
                val onlineList = it.onlineList
                if(onlineList==null)return@addSource
                val onVideoWindowList = onlineList.filter {
                    it.number in layout
                }
                onVideoWindowList.forEach {
                    it.isOnVideo = true
                }
                val windowOnVideo = arrayListOf<LinkedUser>()
                for(i in layout.indices){
                    var userOnVideo:LinkedUser?=null
                    userOnVideo = onVideoWindowList.firstOrNull { it.number == layout[i] }
                    if(userOnVideo==null){
                        userOnVideo = LinkedUser()
                        userOnVideo?.number = -1
                        windowOnVideo.add(userOnVideo)
                    }else{
                        windowOnVideo.add(userOnVideo)
                    }
                }
                //只有不同的时候才更新
                var hasChanged = false
                if (windowOnVideo.size==value?.size ?: 0 ) {
                    for (i in windowOnVideo.indices) {
                        if(windowOnVideo[i] != value?.get(i)){
                            hasChanged = true
                            break
                        }
                    }
                }else{
                    hasChanged = true
                }
                if(hasChanged){
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
                if(layout.size>0){
                    if(layout[0]!=value){
                        postValue(layout[0])
                    }
                }
            }
        }
    }
    /**
     * 申请发言用户
     * */
    val applySpeakUsers:MutableLiveData<List<ApplySpeakUser>> by lazy {
        MutableLiveData()
    }


    /**
     * 会议信息
     * */
    val meetingInfoZq:MutableLiveData<MeetingInfoZQ> by lazy {
        MutableLiveData()
    }

    /**
     *
     */
    var mApplySpeakListenLoopDisposable:Disposable?=null


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
                isShowLoading.postValue(OneTimeEvent(false))
                meetingInfo.postValue(it)
            }, {
                isShowLoading.postValue(OneTimeEvent(false))
                logPrint2File(it)
            })
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
                logPrint2File(it)
            })
    }


    fun startLoopMeetingInfoZQ(){
        mLoopMeetingInfoZQDisposable?.dispose()
        mLoopMeetingInfoZQDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
               ZQManager.loadMeetingInfo()
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                meetingInfoZq.postValue(it)
            }, {
                logPrint2File(it)
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
                    logPrint2File(it)
                })
        )
    }

    fun overMeeting() {
        mDisposables.add(
            ZQManager.exitMeeting()
                .subscribeOn(Schedulers.io())
                .subscribe({
                }, {
                    logPrint2File(it)
                })
        )
    }


    fun loadApplySpeakUsers(){
        ApplySpeakManager.addApplySpeakUser(ApplySpeakUser(1,"悟空","撒亚人"))
        ApplySpeakManager.addApplySpeakUser(ApplySpeakUser(2,"悟饭","撒亚人"))
        ApplySpeakManager.addApplySpeakUser(ApplySpeakUser(3,"悟天","撒亚人"))
        ApplySpeakManager.addApplySpeakUser(ApplySpeakUser(4,"比达","撒亚人"))
    }

    /**
     * 开启申请发言列表监听
     * */
    fun startApplySpeakListen(){
        mApplySpeakListenLoopDisposable?.dispose()
        mApplySpeakListenLoopDisposable = Observable.interval(1000,TimeUnit.MILLISECONDS)
            .subscribe({
                val users = ApplySpeakManager.getApplySpeakUsers()
                applySpeakUsers.postValue(users)
            },{
                logPrint2File(it)
            })
    }

    /**
     * 停止申请发言列表监听
     * */
    fun stopApplySpeakListen(){
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
                            computerWindow.sources,
                            computerWindow.sourcesCmd
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
                    logPrint2File(it)
                })
        )
    }


    fun toggleRecord() {
        val curState = meetingInfo.value?.recordState ?: Constant.RECORD_STOP
        var nextState = when (curState) {
            Constant.RECORD_STOP -> Constant.RECORD_RECORDING
            else -> Constant.RECORD_STOP
        }
        mDisposables.add(
            RecordManager.controlRecord(nextState)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
                })
        )
    }

    fun toggleLive() {
        mDisposables.add(
            RecordManager.controlLiving(meetingInfo.value?.isLiving?.not() ?: false)
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
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
                logPrint2File(it)
            })
    }

    override fun onCleared() {
        super.onCleared()
        exitMeeting.removeSource(meetingInfo)
        isPluginComputer.removeSource(curSceneSources)
        isRecording.removeSource(meetingInfo)
        onVideoWindow.removeSource(interacInfo)
        videoLayoutCount.removeSource(interacInfo)
        mLoopMeetingInfoDisposable?.dispose()
        mLoopCurSceneSourcesDisposable?.dispose()
        mLoopInteracInfoDisposable?.dispose()
        mApplySpeakListenLoopDisposable?.dispose()
        mLoopMeetingInfoZQDisposable?.dispose()
    }


    fun setVideoLayout(usersOnVideo: List<Int>) {
        mDisposables.add(
            InteracManager.setVideoLayout(usersOnVideo.size,usersOnVideo)
                .subscribeOn(Schedulers.io())
                .subscribe({

                },{
                    logPrint2File(it)
                })
        )
    }
}