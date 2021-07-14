package cn.com.ava.zqproject.ui

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.rxjava.RetryFunction
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.entity.LayoutButtonInfo
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import io.reactivex.schedulers.Schedulers


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

    fun resetWindowLayout() {
        luboWindows.postValue(emptyList())
        luboLayouts.postValue(emptyList())
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
}