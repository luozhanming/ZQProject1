package cn.com.ava.lubosdk.zq

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil

/**
 * 创建会议
 * @param theme
 * @param password
 *
 * */
class CreateMeetingZQControl(
    val confName:String,
    val confPassword:String,
    val waitingRoomEnable:Boolean,/*开启等候室*/
    val users: Array<String>,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)?=null
) :IControl {
    override val name: String
        get() = "CreateMeetingZQ"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String,String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username?:""
            this["pswd"]= EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password?:"")
            this["command"] = "1"
            val usersString = StringBuffer()
            users.forEachIndexed {i,it->
                usersString.append("${it}")
                if(i!=users.size-1){
                    usersString.append(";")
                }
            }

            val data = "confCtrl_create_userList=${usersString},videoCodec=264,confType=cloudCtrlMode,streamMode=SINGLE,internalCloud=0,confName=${confName},confPassword=${confPassword},specialConf=,waitingRoomEnable=${if(waitingRoomEnable)1 else 0 }"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }

    override fun build(response: String): Boolean {
        return "=ok" in response
    }
}