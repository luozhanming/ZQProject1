package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * 设置特效时间
 * */
class SetEffectTimeControl(
    val time: Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {
    override val name: String
        get() = "SetEffectTime"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "3"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["stuntTime"] = time.toString()
        }
    }

    override fun build(response: String): Boolean {
        return "-1" !in response
    }
}