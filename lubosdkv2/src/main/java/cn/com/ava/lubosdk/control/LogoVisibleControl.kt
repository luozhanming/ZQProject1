package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.entity.LogoInfo
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * Logo显示控制
 * */
class LogoVisibleControl(
    val logoInfo: LogoInfo,
    val isVisible: Boolean,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "LogoVisible"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "161"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["logovisible"] = if (isVisible) "1" else "0"
            this["x"] = logoInfo.x.toString()
            this["y"] = logoInfo.y.toString()
        }
    }
}