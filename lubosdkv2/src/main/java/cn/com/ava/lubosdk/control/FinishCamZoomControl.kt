package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * 结束云台云镜变焦[BeginCamZoomControl]
 * */
class FinishCamZoomControl(
    val idx:Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IControl {

    override val name: String
        get() = "BeginCamZoom"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "9"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] =idx.toString()
            this["val"] = "100"
        }
    }
}