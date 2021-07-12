package cn.com.ava.zqproject.net

import cn.com.ava.common.http.BaseHttpApi
import io.reactivex.functions.Function
import okhttp3.Interceptor
import okhttp3.OkHttpClient

object PlatformApi : BaseHttpApi() {


    override fun configOkHttpClient(builder: OkHttpClient.Builder) {

    }

    override fun getInterceptors(): List<Interceptor>? {
        return null
    }

    override fun <T> getApiErrorHandler(): Function<T, T> {
        return Function { it ->
            it
        }
//        return Function { response -> //response中code码不会0 出现错误
//            if (response is TecentBaseResponse && (response as TecentBaseResponse).showapiResCode !== 0) {
//                val exception: ExceptionHandle.ServerException = ServerException()
//                exception.code = (response as TecentBaseResponse).showapiResCode
//                exception.message =
//                    if ((response as TecentBaseResponse).showapiResError != null) (response as TecentBaseResponse).showapiResError else ""
//                throw exception
//            }
//            response
//        }
    }
}