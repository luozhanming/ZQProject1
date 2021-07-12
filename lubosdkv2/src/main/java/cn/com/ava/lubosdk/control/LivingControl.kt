package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 直播控制
 * */
class LivingControl(
    val isLiving: Boolean,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "LivingControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = if (isLiving) "1010" else "1011"
            this["key"] = LoginManager.getLogin()?.key ?: ""
        }
    }

    override fun build(response: String): Boolean {
        return "-1" !in response
    }
}