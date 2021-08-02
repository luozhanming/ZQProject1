package cn.com.ava.common.util

import cn.com.ava.common.BuildConfig
import com.blankj.utilcode.util.TimeUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*

/**
 * Log工具类
 * */
object LoggerUtil {

    var logFileDir: String? = null

    init {
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    fun init(path: String) {
        logFileDir = path
    }
}

inline fun Any.logd(message:String){
    Logger.t(this.javaClass.simpleName).d(message)
}

inline fun Any.loge(message:String){
    Logger.t(this.javaClass.simpleName).e(message)
}

inline fun Any.logw(message:String){
    Logger.t(this.javaClass.simpleName).w(message)
}

inline fun Any.logi(message: String){
    Logger.t(this.javaClass.simpleName).i(message)
}

inline fun Any.logPrint2File(throwable: Throwable){
    val file = File(
        "${LoggerUtil.logFileDir}/${
            TimeUtils.date2String(
                Date(),
                "yyyyMMddHHmmss"
            )
        }.txt"
    )
    if (!file.parentFile.exists() && !file.parentFile.mkdirs()) {
        return
    }
    val writer = PrintWriter(FileWriter(file))
    throwable.printStackTrace(writer)
    writer.flush()
    writer.close()
}