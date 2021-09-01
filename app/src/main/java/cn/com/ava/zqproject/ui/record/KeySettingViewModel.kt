package cn.com.ava.zqproject.ui.record

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.entity.LayoutButtonInfo
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import cn.com.ava.lubosdk.util.LayoutButtonHelper
import cn.com.ava.zqproject.common.ComputerModeManager
import cn.com.ava.zqproject.vo.CommandButton
import cn.com.ava.zqproject.vo.LayoutButton
import cn.com.ava.zqproject.vo.StatefulView
import cn.com.ava.zqproject.vo.VideoWindowButton
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class KeySettingViewModel : BaseViewModel() {

    val videoWindowList: MutableLiveData<List<StatefulView<VideoWindowButton>>> by lazy {
        MutableLiveData()
    }

    val videoLayoutList: MutableLiveData<List<StatefulView<LayoutButton>>> by lazy {
        MutableLiveData()
    }

    val presetWindows: MutableLiveData<List<PreviewVideoWindow>> by lazy {
        MutableLiveData()
    }


    /**
     * 用于恢复已选择按钮
     */
    private val selectedKey: MutableList<CommandButton> by lazy {
        arrayListOf()
    }

    /**
     * 获取视频窗口
     * */
    fun getVideoWindowList() {
        if (Cache.getCache().windowsCache.isEmpty()) {  //没加载过
            mDisposables.addAll(
                WindowLayoutManager.getPreviewWindowInfo()
                    .map {
                        val statefuls = arrayListOf<StatefulView<VideoWindowButton>>()
                        it.forEach {
                            val button = VideoWindowButton(it.index, it.windowName)
                            //过滤掉电脑
                            if(!ComputerModeManager.isComputerMode("V${it.index}")){
                                statefuls.add(StatefulView(button))
                            }

                        }
                        statefuls
                    }
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        getPresetList()
                        videoWindowList.postValue(it)
                    }, {
                        logPrint2File(it)
                    })
            )
        } else {
            mDisposables.addAll(Observable.create<List<PreviewVideoWindow>> { e ->
                e.onNext(Cache.getCache().windowsCache)
            }.map {
                val statefuls = arrayListOf<StatefulView<VideoWindowButton>>()
                it.forEach {
                    val button = VideoWindowButton(it.index, it.windowName)
                    //过滤掉电脑
                    if(!ComputerModeManager.isComputerMode("V${it.index}")){
                        statefuls.add(StatefulView(button))
                    }
                }
                statefuls
            }.subscribe({
                getPresetList()
                videoWindowList.postValue(it)
            }, {
                logPrint2File(it)
            }))
        }
    }

    /**
     * 获取布局
     * */
    fun getVideoLayoutList() {
        if(Cache.getCache().layoutInfosCache.isEmpty()){
            mDisposables.add(
                WindowLayoutManager.getLayoutButtonInfo()
                    .map {
                        val statefuls = arrayListOf<StatefulView<LayoutButton>>()
                        it.forEachIndexed { index, layoutButtonInfo ->
                            val button = LayoutButton(
                                index,
                                cn.com.ava.zqproject.common.LayoutButtonHelper
                                    .getLayoutDrawable(layoutButtonInfo.cmd),
                                layoutButtonInfo.cmd
                            )
                            if(!ComputerModeManager.isComputerMode(button.layoutCmd)){
                                statefuls.add(StatefulView(button))
                            }
                        }
                        statefuls
                    }.subscribeOn(Schedulers.io())
                    .subscribe({
                        videoLayoutList.postValue(it)
                    }, {
                        logPrint2File(it)
                    })
            )
        }else{
            mDisposables.add(
               Observable.create<List<LayoutButtonInfo>> {e->
                   e.onNext( Cache.getCache().layoutInfosCache)
               }
                    .map {
                        val statefuls = arrayListOf<StatefulView<LayoutButton>>()
                        it.forEachIndexed { index, layoutButtonInfo ->
                            val button = LayoutButton(
                                index,
                                cn.com.ava.zqproject.common.LayoutButtonHelper
                                    .getLayoutDrawable(layoutButtonInfo.cmd),
                                layoutButtonInfo.cmd
                            )
                            if(!ComputerModeManager.isComputerMode(button.layoutCmd)){
                                statefuls.add(StatefulView(button))
                            }
                        }
                        statefuls
                    }.subscribeOn(Schedulers.io())
                    .subscribe({
                        videoLayoutList.postValue(it)
                    }, {
                        logPrint2File(it)
                    })
            )
        }

    }

    fun getPresetList() {
        //过滤出只有预置位的
        val windows = Cache.getCache().windowsCache.filter {
            it.isPtz
        }
        presetWindows.postValue(windows)

    }

    fun addSelectButton(button: CommandButton): Boolean {
        val selectedSize = selectedKey.size
        if(selectedKey.contains(button)){  //需要移除
            selectedKey.remove(button)
            logd(selectedKey.toString())
            return true
        }else{
            if (selectedSize >= 9) {  //按钮加满了
                logd(selectedKey.toString())
                return false
            } else {
                selectedKey.add(button)
                logd(selectedKey.toString())
                return true
            }
        }
    }


    fun getSelectedButton():List<CommandButton>{
        return selectedKey
    }


}