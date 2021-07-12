package cn.com.ava.common.http

import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 网络HttpApi基类,基类是单例
 *
 */
abstract class BaseHttpApi {

    companion object {
        private val mRetrofitCache: MutableMap<String, Retrofit> by lazy {
            hashMapOf()
        }
    }

    private val mGson: Gson by lazy {
        Gson()
    }

    fun <T> createService(retrofit: Retrofit, service: Class<T>): T =
        retrofit.create(service)

    protected fun getRetrofit(baseUrl: String): Retrofit {
        var retrofit = mRetrofitCache[baseUrl]
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .client(getOkHttpClient())
                .build()
        }
        return retrofit!!
    }

    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val interceptors = getInterceptors()
        interceptors?.forEach {
            builder.addInterceptor(it)
        }
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)
        configOkHttpClient(builder)
        return builder.build()
    }

    /**
     * 自定义设置OkHttpClient
     *
     */
    protected abstract fun configOkHttpClient(builder: OkHttpClient.Builder)

    /**
     * @return 返回特定的Interceptor
     */
    protected abstract fun getInterceptors(): List<Interceptor>?

    protected abstract fun <T> getApiErrorHandler(): Function<T, T>

    fun <T> applySchedulers(observer: Observer<T>): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> ->
            val observable = upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(getApiErrorHandler())
                .onErrorResumeNext(HttpErrorHandler<T>())
            observable.subscribe(observer)
            return@ObservableTransformer observable
        }
    }


}
