package cn.com.ava.lubosdk.zq

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
/**
 * 申请发言（听课)
 * */
class RequestSpeakZQControl(
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)?
) :IControl {
    override val name: String
        get() = "ApplySpeakZQControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String,String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username?:""
            this["pswd"]= EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password?:"")
            this["command"] = "1"
            val data = "confFunc_requestSpeak"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }

    override fun build(response: String): Boolean {
        return "=ok" in response
    }
}