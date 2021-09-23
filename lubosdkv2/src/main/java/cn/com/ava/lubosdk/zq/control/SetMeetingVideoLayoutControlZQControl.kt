package cn.com.ava.lubosdk.zq.control

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
/**
 * 云会控模式布局设置
 * */
class SetMeetingVideoLayoutControlZQControl(
    val streamCount: Int,/*新的布局窗口数*/
    val preLayout: List<Int>,/*以前的布局（内容为互动成员号)*/
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IControl {
    override val name: String
        get() = "SetMeetingVideoLayoutControlZQControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String,String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username?:""
            this["pswd"]= EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password?:"").lowercase()
            this["command"] = "1"
            val userString = StringBuffer()
            preLayout.forEachIndexed {i,it->
                userString.append("_${it}")
            }
            val data = "videoBlender_CloudCtrlModeLayout_D${streamCount}${userString}"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }

    override fun build(response: String): Boolean {
        return "=ok" in response
    }
}