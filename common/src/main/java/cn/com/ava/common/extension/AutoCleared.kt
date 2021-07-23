package cn.com.ava.common.extension

import android.app.Activity
import android.app.Service
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.reflect.KProperty

class AutoClearedValue<T> {

    private var _value: T? = null


    constructor(fragment: Fragment) {
        fragment.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                _value = null
            }
        })
    }

    constructor(activity: AppCompatActivity) {
        activity.lifecycle.addObserver(object :LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun destroy(){
                _value = null
            }
        })
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return _value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        _value = value
    }

}

fun <T:Any?> AppCompatActivity.autoCleared() = AutoClearedValue<T>(this)
fun <T : Any?> Fragment.autoCleared() = AutoClearedValue<T>(this)
