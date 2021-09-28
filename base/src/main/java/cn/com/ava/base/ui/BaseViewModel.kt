package cn.com.ava.base.ui

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.com.ava.common.util.logd
import com.blankj.utilcode.util.Utils
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel: ViewModel() {

    protected val mDisposables:CompositeDisposable by lazy {
        CompositeDisposable()
    }



    override fun onCleared() {
        logd("onCleared()")
        super.onCleared()
        mDisposables.dispose()
        mDisposables.clear()
    }


    fun getResources():Resources{
        return Utils.getApp().resources
    }


}