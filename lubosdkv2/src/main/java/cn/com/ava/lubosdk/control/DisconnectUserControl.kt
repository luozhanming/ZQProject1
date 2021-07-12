package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil

/**
 * 断开某用户互动
 * */
class DisconnectUserControl(
    val username:String,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {


    override val name: String
        get() = "DisconnectUser"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "164"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["operation"] = "0"
            this["username"] =  URLHexEncodeDecodeUtil.stringToHexEncode(username)
        }
    }

    override fun build(response: String): Boolean {
        return "-1" !in response
    }
}