package cn.com.ava.zqproject.ui

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.entity.LayoutButtonInfo
import cn.com.ava.lubosdk.entity.LuBoInfo
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * 录播共享数据ViewModel
 */
class LuBoShareViewModel : BaseViewModel() {

    /**
     * 录播预览窗口
     */
    val luboWindows: MutableLiveData<List<PreviewVideoWindow>> by lazy {
        MutableLiveData()
    }

    /**
     * 录播布局
     */
    val luboLayouts: MutableLiveData<List<LayoutButtonInfo>> by lazy {
        MutableLiveData()
    }

    /**
     * 录播信息
     **/
    val luboInfo: MutableLiveData<LuBoInfo> by lazy {
        MutableLiveData()
    }

    fun resetWindowLayout() {
        luboWindows.postValue(emptyList())
        luboLayouts.postValue(emptyList())
        luboInfo.postValue(null)
    }

    fun loadWindow() {
        mDisposables.add(
            WindowLayoutManager.getPreviewWindowInfo()
                .retryWhen(RetryFunction(5))
                .subscribeOn(Schedulers.io())
                .subscribe({
                    luboWindows.postValue(it)
                }, {
                    logPrint2File(it)
                })
        )
    }

    fun loadLayout() {
        mDisposables.add(
            WindowLayoutManager.getLayoutButtonInfo()
                .retryWhen(RetryFunction(5))
                .subscribeOn(Schedulers.io())
                .subscribe({
                    luboLayouts.postValue(it)
                }, {//出错
                    logPrint2File(it)
                })
        )
    }


    fun loadLuboInfo() {
        mDisposables.add(Flowable.interval(1000, TimeUnit.MILLISECONDS)
                .flatMap {
                    GeneralManager.getLuboInfo()
                        .retryWhen(RetryFunction(5))
                        .toFlowable(BackpressureStrategy.BUFFER)
                }.subscribeOn(Schedulers.io())
                .subscribe({
                    luboInfo.postValue(it)
                }, {
                    logPrint2File(it)
                })
        )
    }


}