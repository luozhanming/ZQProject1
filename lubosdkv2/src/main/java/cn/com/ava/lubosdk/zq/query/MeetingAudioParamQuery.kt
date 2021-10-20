package cn.com.ava.lubosdk.zq.query

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
import cn.com.ava.lubosdk.zq.entity.MeetingAudioParam

class MeetingAudioParamQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :ISPQuery<MeetingAudioParam>{
    override val name: String
        get() = "MeetingAudioParamQuery"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pswd"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "").lowercase()
            this["command"] = "1"
            val data = "getParam_confAudioParam"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }

    override fun build(response: String): MeetingAudioParam {
        val newResponse = response.replace("getParam_confAudioParam_ret=","")
        val split = newResponse.split("&")
        val param = MeetingAudioParam()
        split.forEach {
            val split1 = it.split("=")
            when(split1[0]){
                "muteAllStatus"->param.muteAllState = split1[1]
                "muteStatus"->param.muteStatus = split1[1]
            }
        }
        return param
    }
}