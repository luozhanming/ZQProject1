package cn.com.ava.zqproject

import android.app.Application
import cn.com.ava.common.util.LoggerUtil
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.common.CommonPreference
import com.blankj.utilcode.util.AppUtils
import com.tencent.mmkv.MMKV
import xcrash.XCrash

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

    }
}