package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * 云台开始控制方向
 * */
class BeginSetCamOrientationControl(
    val idx: Int,/*  窗口实体id*/
    val dir: Int,/*方向 (0左/1上/2下/3右)*/
    val pspeed: Int,/*灵敏度左边数值(1-24)*/
    val tspeed: Int,/*   灵敏度右边数值(1_20)*/
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "BeginSetCamOrientation"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "9"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = idx.toString()
            this["dir"] = dir.toString()
            this["pspeed"] = pspeed.toString()
            this["tspeed"] = tspeed.toString()
        }
    }
}