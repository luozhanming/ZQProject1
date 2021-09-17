package cn.com.ava.lubosdk.zq.control

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
/**
 * 远程摄像头控制
 * */
class RemoteCamControl (val close:Boolean,
                        val numId:String,
                        override var onResult: (Boolean) -> Unit,
                        override var onError: ((Throwable) -> Unit)?=null,
): IControl {

    override val name: String
        get() = "RemoteCamControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String,String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username?:""
            this["pswd"]= EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password?:"").lowercase()
            this["command"] = "1"
            val data = "confFunc_setCameraCtrl_numberId=${numId},disable=${if(close) 1 else 0}"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }

    override fun build(response: String): Boolean {
        return "=ok" in response
    }
}