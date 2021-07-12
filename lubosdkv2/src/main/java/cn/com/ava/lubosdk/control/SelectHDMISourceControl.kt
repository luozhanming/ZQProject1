package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * （主讲）互动选择HDMI源
 * */
class SelectHDMISourceControl(
    val hdmiSource:Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IControl{


    override val name: String
        get() = "SaveVideoPresetPos"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "111"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["video"] = if (hdmiSource == -1) "null" else hdmiSource.toString()
        }
    }

}