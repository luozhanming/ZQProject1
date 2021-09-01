package cn.com.ava.zqproject.ui.meeting

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.entity.ListenerInfo
import cn.com.ava.lubosdk.entity.LocalVideoStream
import cn.com.ava.lubosdk.manager.InteracManager
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.vo.InteractComputerSource
import cn.com.ava.zqproject.vo.InteractComputerSources
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


    val isShowLoading: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    val listenerInfo: MutableLiveData<ListenerInfo> by lazy {
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
            addSource(listenerInfo) {
                if (listenerInfo.value?.interaMode != Constant.INTERAC_MODE_LISTEN) {
                    postValue(true)
                }
            }
        }
    }

    /**
     * 电脑视频源列表
     * */
    val computerSourceList:MutableLiveData<List<InteractComputerSource>> by lazy {
        MutableLiveData()
    }



    /**
     * 上次不是电脑的画面，用于恢复
     * */
    private var lastNonComputerScene:LocalVideoStream?=null



    fun loopLoadListenerInfo() {
        isShowLoading.postValue(OneTimeEvent(true))
        mLoopListenerInfoDisposable =
            Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .flatMap {
                    InteracManager.getListenerInfo()
                        .subscribeOn(Schedulers.io())
                }
                .subscribe({
                    isShowLoading.postValue(OneTimeEvent(false))
                    listenerInfo.postValue(it)
                }, {
                    isShowLoading.postValue(OneTimeEvent(false))
                    logPrint2File(it)
                })
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
                        for(i in sources.indices){
                            if(sources.get(i).contains("HDMI")){
                                val source = InteractComputerSource(computerIndex,isHasMultiSource,curSourceIndex-1==i,this.sources[i],sourcesCmd[i])
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
    }


}