package cn.com.ava.lubosdk.zq.query

import android.text.TextUtils
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
import cn.com.ava.lubosdk.zq.entity.MeetingInfoZQ

/**
 * 查询会议信息
 * */
class MeetingInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : ISPQuery<MeetingInfoZQ> {

    override val name: String
        get() = "MeetingInfoQuery"

    override fun build(response: String): MeetingInfoZQ {
        val data = response.replace("getParam_avaConfCtrl_ret=", "")
        val split = data.split("&")
        val info = MeetingInfoZQ()
        split.forEach {
            val split1 = it.split("=")
            when (split1[0]) {
                "dualStreamMode" -> info.isDualMeeting = "1" == split1[1]
                "interaProxyMode"-> info.interaProxyMode = "1"==split1[1]
                "confMaxShowNum"->info.confMaxShowNum = if(TextUtils.isDigitsOnly(split1[1]))split1[1].toInt() else 0
                "sipCourseMode"->info.sipCourseMode = if(TextUtils.isDigitsOnly(split1[1]))split1[1].toInt() else 0
                "waitingRoomEnable"->info.waitingRoomEnable = "1"==split1[1]
                "confMode"->info.confMode = split1[1]
                "confStatus"->info.confStatus = split1[1]
                "confType"->info.confType = split1[1]
                "confInfo"->{
                    if(!TextUtils.isEmpty(split1[1])){
                        val split2 = split1[1].split("_")
                        split2.forEachIndexed { index, s ->
                            if(index==0)info.confId = s
                            else if(index==1)info.confpsw = s
                            else if(index==2)info.confTheme = s
                        }
                    }
                }
                "confStartTime"->info.confStartTime = split1[1]
                "confErrorCode"->info.confErrorCode = split1[1]
            }
        }
        return info

    }

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pswd"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "").lowercase()
            this["command"] = "1"
            val data = "getParam_avaConfCtrl"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }
}