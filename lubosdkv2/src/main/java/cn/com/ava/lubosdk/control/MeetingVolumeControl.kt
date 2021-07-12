package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil

class MeetingVolumeControl(
        val level: Int,
        val otherVolumeInfo:String,
        override var onResult: (Boolean) -> Unit, override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "MeetingVolume"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "40"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = "16"
            val data = otherVolumeInfo.replace("^[MASTER=][\\d]*?".toRegex(),"MASTER=$level")
            this["extvol_i"] = URLHexEncodeDecodeUtil.stringToHexEncode("MASTER=$level,EXTLINEIN1=0,EXTLINEIN2=0,EXTREMOTE=0,LINEIN1=0,LINEIN2=0,MICIN1=0,MICIN2=0","UTF-8")
        }
    }
}