package cn.com.ava.zqproject

import android.os.Environment

object AppConfig {

    const val SP_COMMON_NAME = "common"

    //APP外部存储目录
    val EXTERNAL_FILE_DIR = "${Environment.getExternalStorageDirectory().absolutePath}/AVA_ZQ"

    //APP崩溃记录存储目录
    val CRASH_LOG_DIR = "${EXTERNAL_FILE_DIR}/Crash"

    //APP异常log存储目录
    val EXCEPTION_LOG_DIR = "${EXTERNAL_FILE_DIR}/Exception"




}