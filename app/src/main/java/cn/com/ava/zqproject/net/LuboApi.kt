package cn.com.ava.zqproject.net

import cn.com.ava.common.http.BaseHttpApi
import io.reactivex.functions.Function
import okhttp3.Interceptor
import okhttp3.OkHttpClient

object LuboApi:BaseHttpApi() {
    override fun configOkHttpClient(builder: OkHttpClient.Builder) {

    }

    override fun getInterceptors(): List<Interceptor>? {
        return null
    }

    override fun <T> getApiErrorHandler(): Function<T, T> {
        return Function { it->
            it
        }
    }
}