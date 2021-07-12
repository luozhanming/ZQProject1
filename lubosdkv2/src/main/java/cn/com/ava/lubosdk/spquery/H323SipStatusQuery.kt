package cn.com.ava.lubosdk.spquery

import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.H323SipStatus
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil

class H323SipStatusQuery(override var onResult: (QueryResult) -> Unit,
                         override var onError: ((Throwable) -> Unit)? = null) : ISPQuery<H323SipStatus> {
    override val name: String
        get() = "H323SipStatus"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "9"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pssword"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password
                    ?: "").toLowerCase()
            this["command"] = "1"
            val data = "getParam_avaConfH323Sip"
            this["data"] = EncryptUtil.bytes2HexString(data.toByteArray()).toLowerCase()
        }
    }

    /*getParam_avaConfH323Sip_ret=h323RegStatus=0&sipRegStatus=0&tlsEnable=0&srtpEnable=0&shareDualStreamStatus=0&h323RegisterInfo=gatekeeper=,h323Alias=,h323Ext=&
    sipRegisterInfo=username=,proxyAddr=,registerAddr=,authentication=,password=
    &nickname=LZM11
*/
    override fun build(response: String): H323SipStatus {
        val replace = response.replace("getParam_avaConfH323Sip_ret=", "")
        val split = replace.split("&")
        val status = H323SipStatus()
        split.forEach {
            if (it.startsWith("h323RegStatus=")) {
                val content = it.replace("h323RegStatus=", "")
                status.h323RegStatus = content.toInt()
            } else if (it.startsWith("sipRegStatus=")) {
                val content = it.replace("sipRegStatus=", "")
                status.sipRegStatus = content.toInt()
            } else if (it.startsWith("srtpEnable=")) {
                val content = it.replace("srtpEnable=", "")
                status.srtpEnable = "1" == content
            } else if (it.startsWith("shareDualStreamStatus=")) {
                val content = it.replace("shareDualStreamStatus=", "")
                status.shareDualStreamStatus =content.toInt()
            }else if (it.startsWith("h323RegisterInfo=")) {
                val content = it.replace("h323RegisterInfo=", "")
                val split1 = content.split(",")
                val h323info = H323SipStatus.H323RegisterInfo()
                split1.forEach {
                    val split2 = it.split("=")
                    when(split2[0]){
                        "gatekeeper"->h323info.gatekeeper = split2[1]
                        "h323Alias"->h323info.h323Alias = split2[1]
                        "h323Ext"->h323info.h323Ext = split2[1]
                    }
                }
                status.h323RegisterInfo = h323info
            }else if (it.startsWith("sipRegisterInfo=")) {
                val content = it.replace("sipRegisterInfo=", "")
                val split1 = content.split(",")
                val sipinfo = H323SipStatus.SipRegisterInfo()
                split1.forEach {
                    val split2 = it.split("=")
                    when(split2[0]){
                        "username"->sipinfo.username = split2[1]
                        "proxyAddr"->sipinfo.proxyAddr = split2[1]
                        "registerAddr"->sipinfo.registerAddr = split2[1]
                        "authentication"->sipinfo.authentication = split2[1]
                        "password"->sipinfo.password = split2[1]
                    }
                }
                status.sipRegisterInfo = sipinfo
            }else if(it.startsWith("nickname")){
                val content = it.replace("nickname=", "")
                status.nickname =content
            }
        }
        return status
    }
}