package cn.com.ava.zqproject.extension

import android.os.Handler
import cn.com.ava.base.ui.BaseActivity
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.http.ServerException

fun Throwable.toServerException(): ServerException? {
    return if (this is ServerException) {
        this
    } else {
        null
    }
}


fun BaseFragment<*>.getMainHandler(): Handler? {
    if (activity is BaseActivity<*>) {
        return (activity as BaseActivity<*>).mHandler
    } else return null
}