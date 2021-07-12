package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

class SetPreviewLayoutControl(val layout:String,
                              override var onResult: (Boolean) -> Unit,
                              override var onError: ((Throwable) -> Unit)? = null
):IControl {


    override val name: String
        get() = "SetPreviewLayout"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            var cmd = layout
            this["action"] = "1"
            this["cmd"] = "1"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            if (cmd.contains("&")) {
                cmd = cmd.replace("&", "$")
            }
            this["layout"] = cmd
        }
    }
}