package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.KeyInvalidException
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil

class NewAutoTrackControl(
        val autoTrack:Int,/*1=自动，0=手动，2=半自动*/
        override var onResult: (Boolean) -> Unit,
                          override var onError: ((Throwable) -> Unit)?=null) :IControl {

    companion object{
        const val AUTO = 1
        const val MID = 2
        const val HAND = 0
    }



    override val name: String
        get() = "NewAutoTrack"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pssword"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password
                    ?: "").toLowerCase()
            this["command"] = "1"
            this["data"] =
                    EncryptUtil.bytes2HexString("autoTrack_setParam_enable=$autoTrack".toByteArray()).toLowerCase()
        }
    }


    override fun build(response: String): Boolean {
        if(response == "ErrorCode=2"){
            throw KeyInvalidException()
        }
        return "autoTrack_setParam_ret=ok"==response
    }
}