package cn.com.ava.zqproject.net

import android.util.ArrayMap
import androidx.annotation.StringDef
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.vo.PlatformSetting

object PlatformApiManager {
    //********************************定义接口名称常量*************************************/

    /**获取各个接口路径*/
    const val PATH_GET_INTERFACE = "getInterface"

    /**登录页面地址*/
    const val PATH_WEBVIEW_LOGIN = "loginHtmlUrl"

    const val PATH_ADDRESS_LIST = "addressListUrl"

    const val PATH_APP_LATEST_VERSION = "appLatestVersionUrl"

    const val PATH_ASK_FOR_VIDEO_EXTRA_API = "askForVideoExtraUrl"

    const val PATH_CALL_MEETING = "calledMeetingUrl"

    const val PATH_COMUNICATION_GROUP_DELETE = "communicationGroupDeleteUrl"

    const val PATH_COMUNICATION_GROUP_EDIT = "communicationGroupEditUrl"

    const val PATH_COMUNICATION_GROUP_ITEM = "communicationGroupItemUrl"

    const val PATH_COMUNICATION_GROUP_LIST = "communicationGroupListUrl"

    const val PATH_CREATE_MEETING = "createMeetingUrl"

    const val PATH_FTP_INFO = "ftpInfo"

    const val PATH_HEART_BEAT = "heartBeatUrl"

    const val PATH_RECENT_CALL = "recentCallUrl"

    const val PATH_REFRESH_TOKEN = "refreshTokenUrl"

    const val PATH_LOGOUT = "logoutUrl"

    const val PATH_SAVE_FTP_VIDEO = "saveFTPVideoUrl"

    @StringDef(PATH_GET_INTERFACE, PATH_WEBVIEW_LOGIN)
    annotation class PlatformPath

    private val mInterfaceMap: ArrayMap<String, String> by lazy {
        val map = ArrayMap<String, String>()
        map.put(PATH_GET_INTERFACE, "dangjian-basic/public/setting/settingInfoForTP9")
        map
    }


    fun getApiPath(@PlatformPath path: String): String? {
        return mInterfaceMap[path]
    }


    fun setApiPath(setting:PlatformSetting){
        val clazz = setting::class.java
        val fields = clazz.declaredFields
        fields.forEach {
            it.isAccessible = true
            val fieldName = it.name
            val value = it.get(setting) as String?
            mInterfaceMap[fieldName] = value
        }
        logd(mInterfaceMap.toString())
    }


}