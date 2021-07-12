package cn.com.ava.lubosdk.control

import android.text.TextUtils
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.KeyInvalidException
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
/**
 * KP8P面板控制
 * */
class KP8PControl(
        val key: Int, override var onResult: (Boolean) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null
) : IControl {



    override val name: String
        get() = "KP8P key($key)"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username?:""
            this["pssword"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password?:"").toLowerCase()
            this["command"] = "1"
            var data = ""
            if (key != 5) {
                data = String.format("keyPanel_kp8pCtrl_key=%d", key)
            } else {
                data = String.format("keyPanel_kp8pCtrl_key=%d,option=0", key)
            }
            this["data"] = EncryptUtil.bytes2HexString(data.toByteArray()).toLowerCase()
        }
    }


    override fun build(response: String): Boolean {
        if(response == "ErrorCode=2"){
            throw KeyInvalidException()
        }
        return !TextUtils.isEmpty(response) && response.contains("ok")
    }
}