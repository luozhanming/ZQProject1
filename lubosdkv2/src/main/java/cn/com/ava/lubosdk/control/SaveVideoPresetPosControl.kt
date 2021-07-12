package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 保存抠像预置位控制
 * */
class SaveVideoPresetPosControl(
    val idx: Int,
    val presetIndex: Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {


    override val name: String
        get() = "SaveVideoPresetPos"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "14"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = idx.toString()
            this["position"] = (presetIndex-1).toString()
        }
    }
}