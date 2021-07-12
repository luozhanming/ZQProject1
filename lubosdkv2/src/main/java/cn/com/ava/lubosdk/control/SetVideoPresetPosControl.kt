package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.manager.LoginManager

class SetVideoPresetPosControl(
    val window:PreviewVideoWindow,
    val presetIndex:Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {


    override val name: String
        get() = "SetVideoPresetPos"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "13"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = window.ptzIdx.toString()
            this["position"] = (presetIndex-1).toString()
        }
    }


}