package cn.com.ava.zqproject.ui

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel

class MainViewModel : BaseViewModel() {

     val isShowLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }



}