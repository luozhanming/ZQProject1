package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.KeyInvalidException
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil


/**
 * New record switch API.
 * */
class NewRecordSwitchControl(
        val recordSwtich: Int, /*参考recordSwitch*/
        override var onResult: (Boolean) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null) : IControl {


    companion object {
        const val START = 1
        const val STOP = 0
        const val PAUSE = 2
        const val RESUME = 3
    }

    override val name: String
        get() = "RecordSwitch"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pssword"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password
                    ?: "").toLowerCase()
            this["command"] = "1"
            this["data"] =
                    EncryptUtil.bytes2HexString("record_setParam_enable=$recordSwtich".toByteArray()).toLowerCase()
        }
    }

    override fun build(response: String): Boolean {
        if(response == "ErrorCode=2"){
            throw KeyInvalidException()
        }
        return "record_setParam_ret=ok" == response
    }
}