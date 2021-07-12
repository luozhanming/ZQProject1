package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * 设置当前特效
 * */
class SetCurEffectControl(val stunt:String,
                          override var onResult: (Boolean) -> Unit,
                          override var onError: ((Throwable) -> Unit)? = null
):IControl {
    override val name: String
        get() ="SetCurEffect"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "2"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["stunt"] = stunt
        }
    }

    override fun build(response: String): Boolean {
        return "-1" !in response
    }
}