package cn.com.ava.zqproject.net

import cn.com.ava.common.http.BaseHttpApi
import cn.com.ava.zqproject.common.CommonPreference
import io.reactivex.functions.Function
import okhttp3.Interceptor
import okhttp3.OkHttpClient

object PlatformApi : BaseHttpApi() {


    private var platformAddr by CommonPreference(CommonPreference.KEY_PLATFORM_ADDR, "")

    //平台token
    private var token by CommonPreference(CommonPreference.KEY_PLATFORM_TOKEN, "")


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

    fun getService(baseurl: String? = null): PlatformService {
        return createService(getRetrofit(baseurl ?: platformAddr), PlatformService::class.java)
    }


    fun isCanLinkPlatform(): Boolean {
        return PlatformApiManager.getApiPath(PlatformApiManager.PATH_WEBVIEW_LOGIN) != null
    }
}