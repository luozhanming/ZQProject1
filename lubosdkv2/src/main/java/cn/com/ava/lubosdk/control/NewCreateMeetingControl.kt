package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.KeyInvalidException
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil

class NewCreateMeetingControl(
        val theme: String, val password: String,
        val codeMode: String,/*H264/H265*/
        val cofType: String,/*teachMode=授课模式，confMode=会议模式*/
        val streamMode: String,/*SINGLE=单流模式，DUAL=双流模式*/
        val internalCloud:Boolean,
        val users: Array<String>,
        override var onResult: (Boolean) -> Unit, override var onError: ((Throwable) -> Unit)? = null) : IControl {
    override val name: String
        get() = "NewCreateMeeting"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pssword"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password
                    ?: "").toLowerCase()
            this["command"] = "1"
            val userStrBuffer = StringBuffer().apply {
               users.forEachIndexed{i,user->
                   append(user)
                   if(i!=users.size-1){
                       append(";")
                   }

               }
            }

            val data = "confCtrl_create_userList=${userStrBuffer.toString()}," +
                    "videoCodec=${codeMode},confType=${cofType},streamMode=${streamMode},internalCloud=${if(internalCloud)1 else 0},confName=${theme}," +
                    "confPassword=${password},specialConf=0"
            this["data"] =
                    EncryptUtil.bytes2HexString(data.toByteArray()).toLowerCase()
        }
    }

    override fun build(response: String): Boolean {
        if(response == "ErrorCode=2"){
            throw KeyInvalidException()
        }
        return "confCtrl_create_ret=ok"==response
    }
}