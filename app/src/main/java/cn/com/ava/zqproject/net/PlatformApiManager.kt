package cn.com.ava.zqproject.net

import android.util.SparseArray
import androidx.annotation.IntDef

object PlatformApiManager {
    //********************************定义接口名称常量*************************************/

    /**获取各个接口路径*/
    const val PATH_GET_INTERFACE = 1001

    /**登录页面地址*/
    const val PATH_WEBVIEW_LOGIN = 1002


    @IntDef(PATH_GET_INTERFACE, PATH_WEBVIEW_LOGIN)
    annotation class PlatformPath

    private val mInterfaceMap: SparseArray<String> by lazy {
        val map = SparseArray<String>()
        map.put(PATH_GET_INTERFACE, "")
        map.put(PATH_WEBVIEW_LOGIN,"http://192.168.35.222/#/loginWithPc")
        map
    }


    fun getApiPath(@PlatformPath path: Int): String? {
        return mInterfaceMap[path]
    }


}