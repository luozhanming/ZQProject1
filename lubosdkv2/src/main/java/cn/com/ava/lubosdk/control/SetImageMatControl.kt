package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

class SetImageMatControl(
    val windowIndex: Int,
    val isOn: Boolean,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "SetImageMat"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "94"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = (windowIndex - 1).toString()
            this["enable"] = if (isOn) "enable" else "disable"
        }
    }

}