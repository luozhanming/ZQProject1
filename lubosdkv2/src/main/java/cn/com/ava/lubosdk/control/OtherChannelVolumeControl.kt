package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.entity.VolumeChannel
import cn.com.ava.lubosdk.manager.LoginManager


/**
 * 主音量外的音量控制
 * */
class OtherChannelVolumeControl(
    val volumeChannels:List<VolumeChannel>,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {

    override val name: String
        get() = "OtherChannelVolume"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "40"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = "1"
            volumeChannels.forEachIndexed{i,channel->
                this["name$i"] = channel.channelName
                this["vol$i"] = channel.volumnLevel.toString()
            }
        }
    }
}