package cn.com.ava.zqproject

import android.os.Environment
import com.blankj.utilcode.util.Utils

object AppConfig {




    const val SP_COMMON_NAME = "common"

    //APP外部存储目录
    val EXTERNAL_FILE_DIR = "${Environment.getExternalStorageDirectory().absolutePath}/AVA_ZQ"

    //APP崩溃记录存储目录
    val CRASH_LOG_DIR = "${EXTERNAL_FILE_DIR}/Crash"

    //APP异常log存储目录
    val EXCEPTION_LOG_DIR = "${EXTERNAL_FILE_DIR}/Exception"

    //U盘默认下载路径
    val USB_DEFAULT_DOWNLOAD_PATH = "/AVADownload"


    val PATH_FILE_LOG = "${EXTERNAL_FILE_DIR}/log"

    //MMKV保存路径
    val MMKV_PATH = "${Utils.getApp().filesDir.absolutePath}/mmkv"
    //MMKV密钥
    const val MMKV_CRYPT_KEY = "AVA_ZQ"



    const val WIDTH_APP_DESIGN = 400

    const val MAX_CREATE_MEETING_COUNT = 9

    //平台加密密钥
    val PLATFORM_KEY by lazy {

    }


}