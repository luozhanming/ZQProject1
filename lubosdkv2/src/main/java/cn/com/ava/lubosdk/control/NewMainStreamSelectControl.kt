package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
/**
 * 网页用的推主流
 * */
class NewMainStreamSelectControl(
    val winId: Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "NewMainStreamSelectControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "109"
            this["key"] = LoginManager.getLogin()?.key?:""
            this["video"] = "$winId"

        }
    }


    override fun build(response: String): Boolean {
        return true
    }
}