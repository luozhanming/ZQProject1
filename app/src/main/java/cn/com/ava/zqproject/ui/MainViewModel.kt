package cn.com.ava.zqproject.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.com.ava.lubosdk.entity.LayoutButtonInfo
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.manager.WindowLayoutManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {


    private val mDispoables: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private val isShowLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val luboWindows: MutableLiveData<List<PreviewVideoWindow>> by lazy {
        MutableLiveData()
    }

    val luboLayouts: MutableLiveData<List<LayoutButtonInfo>> by lazy {
        MutableLiveData()
    }


    fun getShowLoading(): MutableLiveData<Boolean> {
        return isShowLoading
    }

    fun resetWindowLayout() {
        luboWindows.postValue(emptyList())
        luboLayouts.postValue(emptyList())
    }


    fun loadWindow(){
        mDispoables.add(WindowLayoutManager.getPreviewWindowInfo()
            .subscribeOn(Schedulers.io())
            .subscribe({
                luboWindows.postValue(it)
            },{

            }))
    }

    fun loadLayout() {
        mDispoables.add(WindowLayoutManager.getLayoutButtonInfo()
            .subscribeOn(Schedulers.io())
            .subscribe({
                luboLayouts.postValue(it)
            }, {//出错

            })
        )
    }

}