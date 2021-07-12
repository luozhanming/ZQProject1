package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * 加入会议
 * */
class JoinMeetingControl(
    val number: String,
    val password: String,
    val type: String,
    val mode: String,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {


    override val name: String
        get() = "JoinMeeting"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "101"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["number"] = number
            this["pswd"] = password
            this["type"] = type
            this["mode"] = mode
        }
    }
}