package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil

class RecordVolumeControl (
    val channelName:String,val channelLevel:Int,
    val raw:String,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)?=null
) : IControl {
    override val name: String
        get() = "RecordVolumeControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String,String>().apply {
            this["action"] = "1"
            this["cmd"] = "40"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = "11"
            val start =  raw.indexOf("${channelName}=")
            val end = raw.indexOf(",", start)
            var subString = ""
            if(end<0){
                subString = raw.substring(
                    raw.indexOf("${channelName}="),
                )
            }else{
                subString = raw.substring(
                    raw.indexOf("${channelName}="),
                    end
                )
            }
            val replace =
                raw.replace(subString, "${channelName}=${channelLevel}")
            this["extvol_n"] = URLHexEncodeDecodeUtil.stringToHexEncode(replace,"UTF-8")

        }
    }
}