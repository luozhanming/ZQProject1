package cn.com.ava.lubosdk.control

import android.text.TextUtils
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.query.InteracVolumeChannelQuery
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
import java.util.regex.Pattern

/**
 * 互动音频控制
 * */
class InteracVolumeControl(
    val channelName:String,val channelLevel:Int,
    var raw:String,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)?=null
) :IControl {
    companion object{
        const val U2_DEFAULT = "MASTER=0,EXTLINEIN1=0,EXTLINEIN2=0,EXTREMOTE=0,LINEIN1=0,LINEIN2=0,MICIN1=0,MICIN2=0,MICIN3=0,HDMIIN=0"
        const val U8A_DEFAULT = "MASTER=0,EXTLINEIN1=0,EXTLINEIN2=0,EXTREMOTE=0,LINEIN1=0,LINEIN2=0,MICIN1=0,MICIN2=0,HDMIIN=0"
        const val U8_DEFAULT = "MASTER=0,LINEIN1=0,LINEIN2=0,MICIN1=0,MICIN2=0"
    }


    override val name: String
        get() = "InteracVolumeControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String,String>().apply {
            this["action"] = "1"
            this["cmd"] = "40"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = "16"
            if(TextUtils.isEmpty(raw)){
                val machineModel = Cache.getCache().luBoInfo.machineModel
                if("u2"==machineModel){
                    raw = InteracVolumeChannelQuery.U2_DEFAULT
                }else if("u8a"==machineModel){
                    raw = InteracVolumeChannelQuery.U8A_DEFAULT
                }else{
                    raw= InteracVolumeChannelQuery.U8_DEFAULT
                }

            }
            val start =  raw.lastIndexOf("${channelName}=")
            val end = raw.indexOf(",", start)
            var subString = ""
            if(end<0){
                subString = raw.substring(
                    raw.lastIndexOf("${channelName}="),
                )
            }else{
                subString = raw.substring(
                    raw.lastIndexOf("${channelName}="),
                    end
                )
            }
            val replace =
                raw.replace(subString, "${channelName}=${channelLevel}")
            this["extvol_i"] = URLHexEncodeDecodeUtil.stringToHexEncode(replace,"UTF-8")

        }
    }
}