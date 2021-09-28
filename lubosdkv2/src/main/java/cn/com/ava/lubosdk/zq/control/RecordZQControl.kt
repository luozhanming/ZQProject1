package cn.com.ava.lubosdk.zq.control

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil

/**
 * 政企会议控制录制操作
 * @param control 1=开始录制，0=停止录制，2=暂停录制，3=继续录制（暂停录制后才能调用继续录制指令）
 * */
class RecordZQControl (val control:Int,
                       override var onResult: (Boolean) -> Unit,
                       override var onError: ((Throwable) -> Unit)?=null,
): IControl {

    override val name: String
        get() = "RecordZQControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String,String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username?:""
            this["pswd"]= EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password?:"").lowercase()
            this["command"] = "1"
            val data = "record_setParam_enable=${control}"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }

    override fun build(response: String): Boolean {
        return "=ok" in response
    }
}