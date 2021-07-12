package cn.com.ava.lubosdk.control

import android.text.TextUtils
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.KeyInvalidException
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil

/**
 * 一键呼叫
 * */
class OneKeyCallControl(
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IControl {


    override val name: String
        get() = "OneKeyCall"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username?:""
            this["pssword"] =  EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password?:"").toLowerCase()
            this["command"] = "1"
            this["data"] =
                EncryptUtil.bytes2HexString("confCtrl_connectAll".toByteArray()).toLowerCase()
        }
    }

    override fun build(response: String): Boolean {
        if(response == "ErrorCode=2"){
            throw KeyInvalidException()
        }
        return !TextUtils.isEmpty(response) && response.contains("ok")
    }
}