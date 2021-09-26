package cn.com.ava.lubosdk.zq.control

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil

/**
 * 设置临时昵称
 * */
class SetMeetingNicknameControl(
    val nickname:String,/*用户昵称（UTF8编码，长度0~36个英文字符，一个中文等于3个英文字符，只支持中文、英文、数字、下划线）*/
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IControl {

    override val name: String
        get() = "SetMeetingNicknameControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String,String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username?:""
            this["pswd"]= EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password?:"").lowercase()
            this["command"] = "1"
            val data = "rserver_SetTempNickName_${nickname}"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }

    override fun build(response: String): Boolean {
        return "=ok" in response
    }
}