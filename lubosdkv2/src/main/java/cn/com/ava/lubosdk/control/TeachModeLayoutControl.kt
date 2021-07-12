package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.KeyInvalidException
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil

class TeachModeLayoutControl(
        val params:List<Int>,
        override var onResult: (Boolean) -> Unit,
                             override var onError: ((Throwable) -> Unit)? = null) : IControl {


    override val name: String
        get() = "TeachModeVideoSelect"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pssword"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password
                    ?: "").toLowerCase()
            this["command"] = "1"
            val strBuffer = StringBuffer()
            params.forEachIndexed { index, i ->
                strBuffer.append("a${i}")
                if(index!=params.size-1){
                    strBuffer.append("_")
                }
            }
            val data ="videoBlender_teachModeLayout_${strBuffer.toString()}"
            this["data"] =
                    EncryptUtil.bytes2HexString(data.toByteArray()).toLowerCase()
        }
    }


    override fun build(response: String): Boolean {
        if(response == "ErrorCode=2"){
            throw KeyInvalidException()
        }
        return "videoBlender_teachModeVideoSelect_ret=ok"==response
    }
}