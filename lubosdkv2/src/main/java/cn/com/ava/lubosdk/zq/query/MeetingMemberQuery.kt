package cn.com.ava.lubosdk.zq.query

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
/**
 * 会议成员
 **/
class MeetingMemberQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :ISPQuery<ListWrapper<LinkedUser>> {
    override val name: String
        get() = "MeetingMemberQuery"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pswd"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "").lowercase()
            this["command"] = "1"
            val data = "getParam_userListInfo"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }

    override fun build(response: String): ListWrapper<LinkedUser> {
        val data = response.replace("getParam_userListInfo_ret=","")
        val split = data.split("&")
        var user:LinkedUser?=null
        val list = arrayListOf<LinkedUser>()
        for(i in 3 until split.size){
            val item = split[i]
            val split2 = item.split(",")
            user = LinkedUser()
            split2.forEach {
                val split1 = it.split("=")

                if("userIdx" in split1[0]){

                }else if("username"==split1[0]){
                    user?.username = split1[1]
                }else if("numberId" == split1[0]){
                    user?.number = split1[1].toInt()
                }else if("shortNum"==split1[0]){
                    user?.shortNumer = split1[1]
                }else if("nickname" == split1[0]){
                    user?.nickname = split1[1]
                }else if("onlineState"==split1[0]){
                    user?.onlineState = split1[1].toInt()
                }else if("sessionId" == split1[0]){
                    user?.sessionId = split1[1]
                }else if("role"==split1[1]){
                    user?.role = split1[1]
                }
            }
            list.add(user!!)

        }
        return ListWrapper(list)
    }
}