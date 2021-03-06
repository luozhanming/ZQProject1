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
import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.lubosdk.entity.LocalVideoStream
import cn.com.ava.lubosdk.entity.zq.ApplySpeakUser
import cn.com.ava.lubosdk.entity.zq.MeetingMemberInfo
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.lubosdk.manager.ZQManager
import cn.com.ava.lubosdk.zq.entity.MeetingAudioParam
import cn.com.ava.lubosdk.zq.entity.MeetingInfoZQ
import cn.com.ava.lubosdk.zq.entity.MeetingStateInfoZQ
import cn.com.ava.lubosdk.zq.query.MeetingMemberQuery
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.ApplySpeakManager
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.vo.InteractComputerSource
import cn.com.ava.zqproject.vo.InteractComputerSources
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ListenerViewModel : BaseViewModel() {


    val computerSources: MutableLiveData<InteractComputerSources> by lazy {
        MutableLiveData()
    }


    val curSceneSources: MutableLiveData<List<LocalVideoStream>> by lazy {
        MutableLiveData()
    }

    private var mLoopListenerInfoDisposable: Disposable? = null

    private var mLoopCurSceneSources: Disposable? = null

    private var mLoopMeetingInfoZQDisposable: Disposable? = null

    private var mLoopMeetingMemberInfoDisposable:Disposable?=null

    private var mTimeCountDisposable: Disposable? = null

    private var mLoopMeetingStateZQDisposable:Disposable? = null

    private var mLoopMeetingAudioDisposable:Disposable?=null


    val isShowLoading: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    /**
     * ??????
     * */
    val meetingMembers: MutableLiveData<List<LinkedUser>> by lazy {
        MutableLiveData()
    }


    /**
     * ????????????
     * */
    val meetingState: MutableLiveData<MeetingStateInfoZQ> by lazy {
        MutableLiveData()
    }

//    val listenerInfo: MutableLiveData<ListenerInfo> by lazy {
//        MutableLiveData()
//    }

    /**
     * ???????????????
     * */
    val isControlVisible: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = true
        }
    }

    val canRequestSpeak:MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = true
        }
    }

    /**
     * ????????????
     * */
    private val computerIndex = ComputerModeManager.computerIndex().replace("V", "").toInt()

    /**
     * ??????????????????
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
     * ????????????
     * */
    val exitMeeting: MediatorLiveData<Boolean> by lazy {
        MediatorLiveData<Boolean>().apply {
            addSource(meetingInfoZQ) {
                if (meetingInfoZQ.value?.confMode != "cloudCtrlMode") {
                    postValue(true)
                }
            }
        }
    }


    val meetingInfoZQ: MutableLiveData<MeetingInfoZQ> by lazy {
        MutableLiveData()
    }

    /**
     * ?????????????????????
     * */
    val computerSourceList: MutableLiveData<List<InteractComputerSource>> by lazy {
        MutableLiveData()
    }


    /**
     * ????????????
     * */
    val meetingTime: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    /**
     * ????????????????????????
     * */
    val isInWaittingRoom:MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }


    val meetingAudioParam:MutableLiveData<MeetingAudioParam> by lazy {
        MutableLiveData()
    }


    private var localNumId = -1


    /**
     * ??????????????????????????????????????????
     * */
    private var lastNonComputerScene: LocalVideoStream? = null


    fun startLoopMeetingInfoZQ() {
        isShowLoading.postValue(OneTimeEvent(true))
        mLoopMeetingInfoZQDisposable?.dispose()
        mLoopMeetingInfoZQDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                ZQManager.loadMeetingInfo()
                    .doOnError {
                        logPrint2File(it,"ListenerViewModel#startLoopMeetingInfoZQ")
                    }
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                isShowLoading.postValue(OneTimeEvent(false))
                meetingInfoZQ.postValue(it)
            }, {
                isShowLoading.postValue(OneTimeEvent(false))
                logPrint2File(it,"ListenerViewModel#startLoopMeetingInfoZQ")
            })
    }

    fun startLoopMeetingAudioParam(){
        mLoopMeetingAudioDisposable?.dispose()
        mLoopMeetingAudioDisposable = Observable.interval(1500,TimeUnit.MILLISECONDS)
            .flatMap {
                ZQManager.loadMeetingAudioParam()
                    .doOnError {
                        logPrint2File(it,"ListenerViewModel#startLoopMeetingAudioParam")
                    }
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                meetingAudioParam.postValue(it)
            }, {
                logPrint2File(it,"ListenerViewModel#startLoopMeetingAudioParam")
            })
    }



    fun stopAllLoopDisposable(){
        mLoopMeetingInfoZQDisposable?.dispose()
        mLoopListenerInfoDisposable?.dispose()
        mLoopListenerInfoDisposable = null
        mLoopCurSceneSources?.dispose()
        mLoopCurSceneSources = null
        mLoopMeetingInfoZQDisposable?.dispose()
        mLoopMeetingInfoZQDisposable = null
        mLoopMeetingMemberInfoDisposable?.dispose()
        mLoopMeetingMemberInfoDisposable = null
        mLoopMeetingStateZQDisposable?.dispose()
        mLoopMeetingStateZQDisposable = null
//        mLoopMeetingAudioDisposable?.dispose()
//        mLoopMeetingAudioDisposable = null
    }




    fun loopCurVideoSceneSources() {
        mLoopCurSceneSources = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                InteracManager.getSceneStream(true)
//                    .map {
//                        //????????????????????????
//                        it.filter { stream->
//                            stream.windowIndex!=ComputerModeManager.computerIndex().replace("V","").toInt()
//                        }
//                        it
//                    }
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                curSceneSources.postValue(it)
            }, {
                logPrint2File(it,"ListenerViewModel#loopCurVideoSceneSources")
            })
    }

    fun getComputerSourceInfo() {
        mDisposables.add(
            WindowLayoutManager.getPreviewWindowInfo()
                .map {
                    //??????????????????
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
                    logPrint2File(it,"ListenerViewModel#getComputerSourceInfo")
                })
        )
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
                    logPrint2File(it,"ListenerViewModel#toggleComputer")
                })
        )
    }


    fun exitMeeting() {
        mDisposables.add(
            InteracManager.exitInteraction()
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it,"ListenerViewModel#exitMeeting")
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        isPluginComputer.removeSource(curSceneSources)

    }

    fun loadMeetingMember() {
        //????????????????????????????????????
        mLoopMeetingMemberInfoDisposable = Observable.interval(0,1500,TimeUnit.MILLISECONDS)
            .flatMap {
                ZQManager.loadMeetingMember()
            }.subscribeOn(Schedulers.io())
            .subscribe({
                meetingMembers.postValue(it.datas)
                isInWaittingRoom.postValue(it.localRole==3)
                localNumId = it.localNumberId
            }, {
                logPrint2File(it,"ListenerViewModel#loadMeetingMember")
            })
    }


    /**
     * ??????????????????
     * */
    fun startTimeCount() {
        mTimeCountDisposable?.dispose()
        mTimeCountDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .subscribe({
                meetingInfoZQ.value?.apply {
                    if (!TextUtils.isEmpty(confStartTime)) {
                        val begin = DateUtil.toTimeStamp(confStartTime, "yyyy-MM-dd_HH:mm:ss")
                        val now = System.currentTimeMillis()
                        val diff = now - begin
                        val toDateString = DateUtil.toHourString(diff)
                        meetingTime.postValue(toDateString)
                    }
                }
            }, {
                logPrint2File(it,"ListenerViewModel#startTimeCount")
            })
    }

    /**
     * ??????????????????
     * */
    fun stopTimeCount() {
        mTimeCountDisposable?.dispose()
    }



    /**
     * ??????????????????
     * */
    fun toggleLocalVolumeAudio() {
        meetingState.value?.apply {
            mDisposables.add(Observable.zip(
                ZQManager.setLocalCam(localCameraCtrl),
                ZQManager.setLocalAudio(localCameraCtrl),
                { t1, t2 ->
                    return@zip t1 && t2
                }).subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it,"ListenerViewModel#toggleLocalVolumeAudio")
                })
            )
        }
    }

    fun applySpeak() {
        mDisposables.add(ZQManager.requestSpeak()
            .subscribeOn(Schedulers.io())
            .subscribe({
                if(it){
                    ToastUtils.showShort(getResources().getString(R.string.request_speak_success))
                }else{
                    ToastUtils.showShort(getResources().getString(R.string.request_speak_failed))
                }
            },{
                logPrint2File(it,"ListenerViewModel#applySpeak")
            }))
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
                //????????????????????????????????????
                canRequestSpeak.postValue(it.requestSpeakMode==0)
            }, {
                logPrint2File(it,"ListenerViewModel#startLoopMeetingState")
            })
    }


}