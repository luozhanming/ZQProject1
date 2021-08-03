package cn.com.ava.zqproject

import android.app.Application
import cn.com.ava.common.util.LoggerUtil
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.common.CommonPreference
import com.blankj.utilcode.util.AppUtils
import com.tencent.mmkv.MMKV
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import xcrash.XCrash
import java.io.IOException

class MyApp : Application() {


    override fun onCreate() {
        super.onCreate()
        LoggerUtil.init(AppConfig.EXCEPTION_LOG_DIR)
        XCrash.init(this, XCrash.InitParameters().apply {
            setAppVersion(AppUtils.getAppVersionName())
            setJavaRethrow(true)
            setJavaLogCountMax(10)
            setJavaDumpAllThreadsWhiteList(arrayOf("^main$", "^Binder:.*", ".*Finalizer.*"))
            setJavaDumpAllThreadsCountMax(10)
         //   setJavaCallback(callback)
            setNativeRethrow(true)
            setNativeLogCountMax(10)
            setNativeDumpAllThreadsWhiteList(arrayOf( "^xcrash\\.sample$", "^Signal Catcher$", "^Jit thread pool$", ".*(R|r)ender.*", ".*Chrome.*" ))
            setNativeDumpAllThreadsCountMax(10)
       //     setNativeCallback(callback)
            setAnrRethrow(true)
            setAnrLogCountMax(10)
         //   setAnrCallback(callback)
            setPlaceholderCountMax(3)
            setPlaceholderSizeKb(512)
            setLogDir(AppConfig.CRASH_LOG_DIR)
            setLogFileMaintainDelayMs(1000)
        })
        MMKV.initialize(this, AppConfig.MMKV_PATH)
        RxJavaPlugins.setErrorHandler(Consumer { e ->
            var e = e
            if (e is UndeliverableException) {
                e = e.cause
            }
            if (e is IOException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@Consumer
            }
            if (e is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@Consumer
            }
            if (e is NullPointerException || e is IllegalArgumentException) {
                // that's likely a bug in the application
                //      Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                return@Consumer
            }
            if (e is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                // Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                return@Consumer
            }
            //     Logger.l("Undeliverable exception");
        })

    }
}