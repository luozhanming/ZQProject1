package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
/**
 * （主讲）互动中重拨参与者
 * */
class RedialUserControl(
    val user: String,/*需要重拨的用户名*/
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "RedialUser"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "164"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["operation"] = "1"
            this["username"] = URLHexEncodeDecodeUtil.stringToHexEncode(user)
        }
    }
}