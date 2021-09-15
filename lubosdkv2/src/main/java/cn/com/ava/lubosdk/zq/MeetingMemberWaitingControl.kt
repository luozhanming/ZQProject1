package cn.com.ava.lubosdk.zq

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
/**
 * 入会/等候角色切换
 * */
class MeetingMemberWaitingControl(
    val userList:Array<String>,
    val newRole:String,/*listener=切换为听课，viewer=切换为旁听*/
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)?=null
) : IControl {
    override val name: String
        get() = "MeetingMemberWaitingControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String,String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username?:""
            this["pswd"]= EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password?:"")
            this["command"] = "1"
            val usersString = StringBuffer()
            userList.forEachIndexed {i,it->
                usersString.append("${it}")
                if(i!=userList.size-1){
                    usersString.append(";")
                }
            }
            val data = "confCtrl_ChangeUserRole_newRole=${newRole},userList=${usersString}"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }

    override fun build(response: String): Boolean {
        return "=ok" in response
    }



}