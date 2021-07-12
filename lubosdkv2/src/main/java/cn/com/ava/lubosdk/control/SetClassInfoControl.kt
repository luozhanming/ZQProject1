package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
/**
 * 设置录播课堂信息
 * */
class SetClassInfoControl(
    val theme: String,
    val teacher: String,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {
    override val name: String
        get() = "SetClassInfo"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "7"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["info1"] = URLHexEncodeDecodeUtil.stringToHexEncode(theme, "GBK")
            this["info2"] = URLHexEncodeDecodeUtil.stringToHexEncode(teacher, "GBK")
            this["info3"] = "0"
        }
    }

    override fun build(response: String): Boolean {
        return "-1" !in response
    }
}