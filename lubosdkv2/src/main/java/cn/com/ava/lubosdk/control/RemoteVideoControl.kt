package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 互动远端视频源控制
 * */
class RemoteVideoControl(
    val rid:Int,
    val vid:Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {


    override val name: String
        get() = "RemoteVideo"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "249"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["rid"] = rid.toString()
            this["vid"] = vid.toString()
        }
    }
}