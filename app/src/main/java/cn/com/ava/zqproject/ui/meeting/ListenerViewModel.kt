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
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.lubosdk.entity.LocalVideoStream
import cn.com.ava.lubosdk.entity.zq.ApplySpeakUser
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.lubosdk.manager.ZQManager
import cn.com.ava.lubosdk.zq.entity.MeetingInfoZQ
import cn.com.ava.lubosdk.zq.entity.MeetingStateInfoZQ
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.ApplySpeakManager
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.vo.InteractComputerSource
import cn.com.ava.zqproject.vo.InteractComputerSources
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

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

    private var mTimeCountDisposable: Disposable? = null

    private var mLoopMeetingStateZQDisposable:Disposable? = null


    val isShowLoading: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    val meetingMembers: MutableLiveData<List<LinkedUser>> by lazy {
        MutableLiveData()
    }


    /**
     * 会议状态
     * */
    val meetingState: MutableLiveData<MeetingStateInfoZQ> by lazy {
        MutableLiveData()
    }

//    val listenerInfo: MutableLiveData<ListenerInfo> by lazy {
//        MutableLiveData()
//    }

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
     * 电脑视频源列表
     * */
    val computerSourceList: MutableLiveData<List<InteractComputerSource>> by lazy {
        MutableLiveData()
    }


    /**
     * 会议时间
     * */
    val meetingTime: MutableLiveData<String> by lazy {
        MutableLiveData()
    }


    /**
     * 上次不是电脑的画面，用于恢复
     * */
    private var lastNonComputerScene: LocalVideoStream? = null


    fun startLoopMeetingInfoZQ() {
        isShowLoading.postValue(OneTimeEvent(true))
        mLoopMeetingInfoZQDisposable?.dispose()
        mLoopMeetingInfoZQDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                ZQManager.loadMeetingInfo()
            }.retryWhen(RetryFunction(Int.MAX_VALUE))
            .subscribeOn(Schedulers.io())
            .subscribe({
                isShowLoading.postValue(OneTimeEvent(false))
                meetingInfoZQ.postValue(it)
            }, {
                isShowLoading.postValue(OneTimeEvent(false))
                logPrint2File(it)
            })
    }

    fun stopLoopMeetingInfoZQ() {
        mLoopMeetingInfoZQDisposable?.dispose()
    }


    fun loopCurVideoSceneSources() {
        mLoopCurSceneSources = Observable.interval(1000, TimeUnit.MILLISECONDS)
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
            }.subscribe({
                curSceneSources.postValue(it)
            }, {
                logPrint2File(it)
            })
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


    fun exitMeeting() {
        mDisposables.add(
            InteracManager.exitInteraction()
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        isPluginComputer.removeSource(curSceneSources)
        mLoopListenerInfoDisposable?.dispose()
        mLoopListenerInfoDisposable = null
        mLoopCurSceneSources?.dispose()
        mLoopCurSceneSources = null
        mLoopMeetingInfoZQDisposable?.dispose()
        mLoopMeetingInfoZQDisposable = null
    }

    fun loadMeetingMember() {
        mDisposables.add(ZQManager.loadMeetingMember()
            .subscribeOn(Schedulers.io())
            .subscribe({
                meetingMembers.postValue(it.datas)
            }, {
                logPrint2File(it)
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
                meetingInfoZQ.value?.apply {
                    if (!TextUtils.isEmpty(confStartTime)) {
                        val begin = DateUtil.toTimeStamp(confStartTime, "yyyy-MM-dd_HH:mm:ss")
                        val now = System.currentTimeMillis()
                        val diff = now - begin - 30360*1000
                        val toDateString = DateUtil.toDateString(diff, "HH:mm:ss")
                        meetingTime.postValue(toDateString)
                    }
                }
            }, {
                logPrint2File(it)
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
            mDisposables.add(Observable.zip(
                ZQManager.setLocalCam(localCameraCtrl),
                ZQManager.setLocalAudio(localCameraCtrl),
                { t1, t2 ->
                    return@zip t1 && t2
                }).subscribeOn(Schedulers.io())
                .subscribe({

                }, {
                    logPrint2File(it)
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
                logPrint2File(it)
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
                //处理申请发言的列表数据源
                ToastUtils.showShort(it.toString())
            }, {
                logPrint2File(it)
            })
    }


}