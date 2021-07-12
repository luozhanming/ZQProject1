package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 结束云台控制方向操作
 * */
class FinishSetCamOrientationControl(
    val idx: Int,/*  窗口实体id*/
    val orientation: Int/*方向 (0左/1上/2下/3右)*/,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "FinishSetCamOrientation"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "8"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = idx.toString()
            this["dir"] = orientation.toString()
            this["pspeed"] = "0"
            this["tspeed"] = "0"
        }
    }

}