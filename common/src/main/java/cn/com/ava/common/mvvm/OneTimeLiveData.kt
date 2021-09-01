package cn.com.ava.common.mvvm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class OneTimeLiveData<T> : MutableLiveData<OneTimeEvent<T>>() {


    fun observeOne(owner: LifecycleOwner, func: (T) -> Unit) {
        observe(owner) {
            it.call(func)
        }
    }
}