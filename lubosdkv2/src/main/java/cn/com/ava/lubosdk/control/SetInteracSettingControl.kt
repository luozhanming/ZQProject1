package cn.com.ava.lubosdk.control

import android.text.TextUtils
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

class SetInteracSettingControl(
    val netparam: String = "2048",
    val protocal: String = "0",
    val code: String = "0",
    val asswitch: String = "0",
    val innerCloud: Boolean,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "SetPresetFocus"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "104"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["netparam"] = if (TextUtils.isEmpty(netparam)) "0" else netparam
            this["codemode"] = if (TextUtils.isEmpty(code)) "0" else code
            this["protocal"] = if (TextUtils.isEmpty(protocal)) "0" else protocal
            this["enableCloud"] = if (innerCloud) "1" else "0"
            this["asswitch"] = if (TextUtils.isEmpty(asswitch)) "0" else asswitch
        }
    }

}