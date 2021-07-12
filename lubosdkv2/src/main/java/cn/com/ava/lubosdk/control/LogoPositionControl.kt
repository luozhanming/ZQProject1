package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

/**
 *Logo显示位置控制
 * */
class LogoPositionControl(
    val x: Int,
    val y: Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {


    override val name: String
        get() = "LogoPosition"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] ="161"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["logovisible"] = "-1"
            this["x"] = x.toString()
            this["y"] = y.toString()
        }
    }
}