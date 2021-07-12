package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * 关机
 * */
class TurnOffMachineControl(
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IControl {



    override val name: String
        get() = "TurnOffMachine"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "84"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = "2"
        }
    }
}