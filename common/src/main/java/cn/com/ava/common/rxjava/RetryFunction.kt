package cn.com.ava.common.rxjava

import cn.com.ava.common.http.ResponseThrowable
import cn.com.ava.common.http.ServerException
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

class RetryFunction(val maxRetry: Int = 5) : Function<Observable<Throwable>, ObservableSource<*>> {

    private var retryCount: Int = 0

    override fun apply(t: Observable<Throwable>): ObservableSource<*> {
       return t.flatMap { throwable->
            if(++retryCount>maxRetry){  //到达最大重试次数抛出异常
                return@flatMap Observable.error(throwable)
            }
            return@flatMap Observable.timer(500,TimeUnit.MILLISECONDS)
        }
    }

}