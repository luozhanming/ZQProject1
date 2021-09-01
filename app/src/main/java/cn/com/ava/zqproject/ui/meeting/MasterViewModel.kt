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
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.lubosdk.manager.RecordManager
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.vo.InteractComputerSource
import cn.com.ava.zqproject.vo.InteractComputerSources
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class MasterViewModel : BaseViewModel() {


    val computerSources: MutableLiveData<InteractComputerSources> by lazy {
        MutableLiveData()
    }


    val curSceneSources: MutableLiveData<List<LocalVideoStream>> by lazy {
        MutableLiveData()
    }

    private var mLoopMeetingInfoDisposable: Disposable? = null

    private var mLoopCurSceneSourcesDisposable: Disposable? = null

    private var mLoopInteracInfoDisposable: Disposable? = null


    val isShowLoading: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

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
            addSource(meetingInfo) {
                if (meetingInfo.value?.interaMode != Constant.INTERAC_MODE_CONFERENCE) {
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

    val interacInfo: MutableLiveData<InteraInfo> by lazy {
        MutableLiveData()
    }

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
            InteracManager.exitInteraction()
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
                })
        )
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