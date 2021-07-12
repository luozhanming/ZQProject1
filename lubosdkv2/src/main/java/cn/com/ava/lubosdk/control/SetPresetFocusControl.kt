package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

class SetPresetFocusControl(
    val idx: Int,
    @Constant.FocusLevel val focusLevel:Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {
    override val name: String
        get() = "SetPresetFocus"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "9"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = idx.toString()
            this["zoom"] = "97"
            this["val"] = focusLevel.toString()
        }
    }

    override fun build(response: String): Boolean {
        return "-1" !in response
    }
}