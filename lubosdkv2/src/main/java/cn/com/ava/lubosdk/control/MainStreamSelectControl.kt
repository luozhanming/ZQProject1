package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil

class MainStreamSelectControl(
        val winId:Int,
        override var onResult: (Boolean) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null) : IControl {

    override val name: String
        get() = "MainStreamSelect"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pssword"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password
                    ?: "").toLowerCase()
            this["command"] = "1"
            this["data"] = EncryptUtil.bytes2HexString("videoBlender_mainStreamSelect_${winId}".toByteArray()).toLowerCase()
        }
    }


    override fun build(response: String): Boolean {
        return "videoBlender_mainStreamSelect_ret=ok"==response
    }
}