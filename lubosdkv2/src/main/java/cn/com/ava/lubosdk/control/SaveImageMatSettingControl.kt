package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.entity.ImageMatInfo
import cn.com.ava.lubosdk.manager.LoginManager


/*&
* 保存抠像设置
* */
class SaveImageMatSettingControl(
    val matInfo: ImageMatInfo,
    val index: Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "SaveImageMatSetting"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "93"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["p0"] = matInfo.keyingThreshold1.toString()
            this["p1"] = matInfo.keyingThreshold3.toString()
            this["p2"] = matInfo.keyingBG1.toString()
            this["p3"] = matInfo.keyingBG2.toString()
            this["p4"] = matInfo.keyingThreshold2.toString()
            this["p5"] = matInfo.keyingThreshold4.toString()
            this["p6"] = matInfo.keyingHDMI3VoColor.toString()
            this["p7"] = matInfo.keyingHDMI3VoML.toString()
            this["p8"] = matInfo.keyingHDMI3VoSL.toString()
            this["index"] = index.toString()
        }
    }

    override fun build(response: String): Boolean {
        return "-1" !in response
    }
}