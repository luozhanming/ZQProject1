package cn.com.ava.lubosdk.zq.query

import androidx.core.text.isDigitsOnly
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
import cn.com.ava.lubosdk.zq.entity.MeetingStateInfoZQ

class MeetingStateInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : ISPQuery<MeetingStateInfoZQ> {

    override val name: String
        get() = "MeetingStateInfoQuery"

    override fun build(response: String): MeetingStateInfoZQ {
        val data = response.replace("getParam_avaConfFunc_ret=", "")
        val split = data.split("&")
        val info = MeetingStateInfoZQ()
        split.forEach {
            val split1 = it.split("=")
            when (split1[0]) {
                "requestSpeakAloneStatus" -> {
                    if (split1.size > 1&&split1[1].isNotEmpty()) {
                        val list = arrayListOf<Int>()
                        val split2 = split1[1].split(",")
                        split2.forEach {
                            if (it.isDigitsOnly()) {
                                list.add(it.toInt())
                            }
                        }
                        info.requestSpeakStatus = list
                    }else{
                        info.requestSpeakStatus = emptyList()
                    }
                }
                "requestSpeakAloneRetStatus" -> {
                    info.requestSpeakRetStatus =
                        if (split1[1].isDigitsOnly()) split1[1].toInt() else 0
                }
                "requestSpeakAloneMode" -> {
                    info.requestSpeakMode = if (split1[1].isDigitsOnly()) split1[1].toInt() else 0
                }
                "lockConference" -> info.lockConference = "1" == split1[1]
                "localCameraCtrl" -> info.localCameraCtrl = "0" == split1[1]
                "cameraCtrlState" -> {
                    if (split1.size > 1&&split1[1].isNotEmpty()) {
                        val list = arrayListOf<Int>()
                        val split2 = split1[1].split(",")
                        split2.forEach {
                            if (it.isDigitsOnly()) {
                                list.add(it.toInt())
                            }
                        }
                        info.cameraCtrlState = list
                    }else{
                        info.cameraCtrlState = emptyList()
                    }
                }
                "audioCtrlState" -> {
                    if (split1.size > 1&&split1[1].isNotEmpty()) {
                        val list = arrayListOf<Int>()
                        val split2 = split1[1].split(",")
                        split2.forEach {
                            if (it.isDigitsOnly()) {
                                list.add(it.toInt())
                            }
                        }
                        info.audioCtrlState = list
                    }else{
                        info.audioCtrlState = emptyList()
                    }
                }
            }
        }
        return info

    }

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pswd"] =
                EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "").lowercase()
            this["command"] = "1"
            val data = "getParam_avaConfFunc"
            logd("data = ${data}")
            this["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(data)
        }
    }
}