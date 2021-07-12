package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * 画面字幕滚动控制
 * */
class RollCaptionControl(
    val isRoll: Boolean,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {

    override val name: String
        get() = "RollCaption"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "171"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = "6"
            this["op"] = "DISPTYPE@"
            this["type"] = if (isRoll) "RIGHT2LEFT" else "NULL"
        }
    }
}