package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 分享双流
 * */
class ShareDocControl(
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IControl {


    override val name: String
        get() = "ShareDoc"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "125"
            this["key"] = LoginManager.getLogin()?.key ?: ""
        }
    }
}