package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.KeyInvalidException
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil

class NewLiveControl(
        val isLive: Boolean,
        override var onResult: (Boolean) -> Unit, override var onError: ((Throwable) -> Unit)? = null) : IControl {
    override val name: String
        get() = "NewLive"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pssword"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password
                    ?: "").toLowerCase()
            this["command"] = "1"
            this["data"] =
                    EncryptUtil.bytes2HexString("live_setParam_enable=${if (isLive) 1 else 0}".toByteArray()).toLowerCase()
        }
    }

    override fun build(response: String): Boolean {
        if(response == "ErrorCode=2"){
            throw KeyInvalidException()
        }
        return "live_setParam_ret=ok" == response
    }
}