package cn.com.ava.lubosdk.control.whiteboard

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import com.blankj.utilcode.util.EncryptUtils
/**
 * 清楚白板痕迹
 * */
class CleanWhiteBoardDataControl(val isLocal: Boolean, override var onResult: (Boolean) -> Unit,
                                 override var onError: ((Throwable) -> Unit)? = null) : IControl {
    override val name: String
        get() = "SetWritePermissionControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "6"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pwsd"] = EncryptUtils.encryptMD5ToString(LoginManager.getLogin()?.password).toLowerCase();

            this["command"] = "1"
            this["data"] = "WhiteBoard_CleanAllData_${if (isLocal) "Local" else "All"}"
        }
    }


    override fun build(response: String): Boolean {
        if ("error" in response) return false
        else {
            val split = response.split("=")
            return split.size > 1 && "ok" == split[1]
        }
    }



}