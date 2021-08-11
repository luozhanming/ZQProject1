package cn.com.ava.zqproject.net

import cn.com.ava.common.http.BaseHttpApi
import cn.com.ava.common.http.ServerException
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.vo.PlatformLogin
import cn.com.ava.zqproject.vo.PlatformResponse
import io.reactivex.functions.Function
import okhttp3.Interceptor
import okhttp3.OkHttpClient

object PlatformApi : BaseHttpApi() {


    private var platformAddr by CommonPreference(CommonPreference.KEY_PLATFORM_ADDR, "")

    //平台token
    private var token by CommonPreference(CommonPreference.KEY_PLATFORM_TOKEN, "")

    private var platformLogin: PlatformLogin? = null


    override fun configOkHttpClient(builder: OkHttpClient.Builder) {
    }

    override fun getInterceptors(): List<Interceptor>? {
        val interceptors = arrayListOf<Interceptor>()
        interceptors.add(Interceptor { chain ->
            val request =
                chain.request().newBuilder().addHeader("token", platformLogin?.token ?: "").build()
            chain.proceed(request)

        })
        return interceptors
    }

    override fun <T> getApiErrorHandler(): Function<T, T> {
        return Function { it ->
            if (it is PlatformResponse<*> && !it.success) {
                val exception = ServerException(it.code, it.message)
                throw exception
            }
            it
        }
    }

    fun getService(baseurl: String? = null): PlatformService {
        return createService(getRetrofit(baseurl ?: platformAddr), PlatformService::class.java)
    }


    fun isCanLinkPlatform(): Boolean {
        return PlatformApiManager.getApiPath(PlatformApiManager.PATH_WEBVIEW_LOGIN) != null
    }

    fun refreshLoginToken(token:String){
        platformLogin?.apply {
            this.token = token
        }
    }

    fun getPlatformLogin():PlatformLogin?{
        return platformLogin
    }


    fun login(login: PlatformLogin) {
        platformLogin = login
    }

    fun logout(){
        platformLogin = null
    }
}