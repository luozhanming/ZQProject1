package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * 预览跟踪模式
 * */
class CamTrackControl(
    @Constant.CamTrackMode val trackMode: String,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {

    override val name: String
        get() = "CamTrack"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "38"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["param"] = trackMode
        }
    }

    override fun build(response: String): Boolean {
        return "-1" !in response
    }
}