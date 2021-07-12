package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.VolumeChannel
import cn.com.ava.lubosdk.entity.VolumnSetting
import java.util.*
/**
 * 音频通道信息
 * */
class VolumeChannelsQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IQuery<ListWrapper<VolumeChannel>> {

    override val name: String
        get() = "VolumeChannel"

    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.UI_OUTPUTAUDIO,AVATable.AB_VOLUME,AVATable.UI_INPUTAUDIO,
            AVATable.ARM_VOLUME,AVATable.AB_APMSET_NSEL,AVATable.AB_APMSET_NON,AVATable.ARM_EXTVOL_N
        )

    override var offsets: Int = 0
        get() = field
        set(value) {field = value}

    override fun build(split: List<String>): ListWrapper<VolumeChannel> {
        val setting = VolumnSetting()
        val channels: MutableList<VolumeChannel> =
            ArrayList()
        val mainOutputName = split[0]
        val mainOutputValue = split[1]
        val outputValue = split[6]
        val nsel = split[4]
        val non = split[5]
        var canAutoAmp = false
        if (!TextUtils.isEmpty(nsel)) {
            setting.setApmset_nsel(nsel.toInt())
        }
        if (!TextUtils.isEmpty(non)) {
            canAutoAmp = true
            val split1 = non.split(",".toRegex()).toTypedArray()
            var i = 0
            val len = split1.size
            while (i < len) {
                if (i == 0) {
                    setting.setAutoAmp("1" == split1[i])
                } else if (i == 1) {
                    setting.setNoiseRestrain("1" == split1[i])
                } else if (i == 2) {
                    setting.setEQOpen("1" == split1[i])
                }
                i++
            }
        }
        val masterChannel = VolumeChannel()
        if (TextUtils.isEmpty(mainOutputName)) { //缺省默认名
            masterChannel.channelName = "MASTER"
        } else {
            masterChannel.channelName = mainOutputName
        }
        masterChannel.min = 0
        masterChannel.max = 5
        if (TextUtils.isEmpty(mainOutputValue)) {
            masterChannel.volumnLevel = 0
        } else {
            val split1 =
                outputValue.split(",".toRegex()).toTypedArray()
            for (s in split1) {
                val split2 = s.split("=".toRegex()).toTypedArray()
                if (split2[0] == mainOutputName) {
                    masterChannel.volumnLevel = split2[1].toInt()
                }
            }
        }
        channels.add(masterChannel)

        val otherOutputName = split[2]
        var otherOutputValue = ""
        if (split.size > 3) {
            otherOutputValue = split[3]
        }
        val split1 =
            otherOutputName.split(",".toRegex()).toTypedArray()
        for (s in split1) {
            val channel = VolumeChannel()
            channel.channelName = s
            if (s.contains("MASTER")) {
                channel.min = 0
                channel.max = 5
            } else if (s.contains("LINEIN1")) {
                channel.max = if (canAutoAmp) 10 else 5
                channel.min = if (setting.isAutoAmp() && setting.getApmset_nsel() === 1) 5 else 0
            } else if (s.contains("LINEIN2")) {
                channel.max = if (canAutoAmp) 10 else 5
                channel.min = if (setting.isAutoAmp() && setting.getApmset_nsel() === 2) 5 else 0
            } else {  //MIC
                channel.min = 0
                channel.max = 5
                channel.isEnable = !setting.isAutoAmp()
            }
            if (TextUtils.isEmpty(outputValue)) {
                channel.volumnLevel = 0
            } else {
                val split3 =
                    outputValue.split(",".toRegex()).toTypedArray()
                for (s1 in split3) {
                    val split2 = s1.split("=".toRegex()).toTypedArray()
                    if (channel.channelName.equals(split2[0])) {
                        channel.volumnLevel = split2[1].toInt()
                        break
                    }
                }
            }
            channels.add(channel)
        }
        return ListWrapper((channels))
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}