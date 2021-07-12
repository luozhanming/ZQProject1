package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 云台云镜开始变焦
 * */
class BeginCamZoomControl(
    val idx: Int,
    @Constant.ZoomOrientation val orientation: Int,
    val speed: Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "BeginCamZoom"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "9"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = idx.toString()
            this["val"] = orientation.toString()
            this["speed"] = speed.toString()
        }
    }
}