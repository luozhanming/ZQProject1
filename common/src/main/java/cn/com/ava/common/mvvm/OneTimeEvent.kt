package cn.com.ava.common.mvvm

class OneTimeEvent<T>(val event:T) {


    private var flag:Boolean = true


    fun call(func:(T)->Unit){
        if(flag){
            flag = false
            func(event)
        }
    }


    fun get(): T? = if (flag) {
        flag = false
        event
    } else {
        null
    }
}