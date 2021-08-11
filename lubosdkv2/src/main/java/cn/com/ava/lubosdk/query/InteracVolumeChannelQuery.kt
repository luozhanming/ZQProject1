package cn.com.ava.lubosdk.query

import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.VolumeChannel
/**
 * 互动音频通道信息
 * */
class InteracVolumeChannelQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<ListWrapper<VolumeChannel>> {




    override val name: String
        get() = "InteracVolumeChannelQuery"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.ARM_EXTVOL_I)
    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
    override var offsets: Int = 0
        get() = field
        set(value) {field = value}

    override fun build(split: List<String>): ListWrapper<VolumeChannel> {
        val extvol_i = split[0]
        val split1 = extvol_i.split(",")
        val channels = arrayListOf<VolumeChannel>()
        split1.forEach {
            val channel = VolumeChannel()
            val split2 = it.split("=")
            channel.channelName = split2[0]
            channel.volumnLevel = split2[1].toIntOrNull()?:0
            channel.max = 5
            channel.min = 0
            if(channel.isSilent()){
                channel.adapteVolumeLevel = channel.volumnLevel-256
            }else{
                channel.adapteVolumeLevel = channel.volumnLevel
            }
            channels.add(channel)
        }
        return ListWrapper(channels)
    }

}