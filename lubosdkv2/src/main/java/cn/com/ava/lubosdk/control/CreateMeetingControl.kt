package cn.com.ava.lubosdk.control

import android.text.TextUtils
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
/**
 * 创建会议
 * */
class CreateMeetingControl(
    val theme: String, val password:String,
    val codeMode: String, val cofType: String,
    val streamMode: String, val maxShow: Int,
    val users: Array<String>,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "CreateMeeting"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "105"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["codeMode"] = codeMode
            this["confType"] = cofType
            this["streamMode"] = streamMode
            this["maxShow"] = maxShow.toString()
            this["confName"] = URLHexEncodeDecodeUtil.stringToHexEncode(theme)
            this["confPassword"] = if (TextUtils.isEmpty(password)) "" else password
            this["userNum"] = users.size.toString()
            users.forEachIndexed { index, user ->
                this["user$index"] = URLHexEncodeDecodeUtil.stringToHexEncode(users[index])
            }
            if ("AXM" == cofType) {
                this["vbcMode"] =
                    if (maxShow == 4) "0" else if (maxShow == 2) "1" else if (maxShow == 6) "2" else "0"
            }
        }
    }


    override fun build(response: String): Boolean {
        val split = response.split("\\|".toRegex())
        return "0" == split[0]
    }

}