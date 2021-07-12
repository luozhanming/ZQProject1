package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

class ShareDocSIPH323Control(
    val isShare:Boolean,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {


    override val name: String
        get() = "ShareDocSIPH323"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "225"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["operate"] = if(isShare)"open" else "close"
        }
    }
}