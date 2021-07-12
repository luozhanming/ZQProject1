package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.MeetingInfo
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.VolumeChannel
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
/**
 * 互动会议信息
 * */
class MeetingInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IQuery<MeetingInfo> {

    override val name: String
        get() = "MeetingInfo"

    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.AT_AUTOTRACK,AVATable.REC_INFO,
        AVATable.RTSP_INFO,AVATable.SEM_INTERAMODE,AVATable.INTERA_MEETINGINFO,
        AVATable.RSC_SETMAXSHOWNUM,AVATable.UI_OUTPUTAUDIO,AVATable.ARM_EXTVOL_I,AVATable.SEM_DUALSTREAMMODE)

    override var offsets: Int = 0
        get() =field
        set(value) {field = value}

    override fun build(split: List<String>): MeetingInfo {
        val info = MeetingInfo()
        val autoTrackStr = split[0]
        val recordStateStr = split[1]
        val liveStateStr = split[2]
        val interacStateStr = split[3]
        val meetingInfoStr = split[4]
        val maxCountStr = split[5]
        val meetingInfo = "{" + split[6] + "}"
        info.trackMode = autoTrackStr
        val recordString =
            recordStateStr.split(" ".toRegex()).toTypedArray()
        if (recordString[0].toInt() == 0 && recordString[1].toInt() == -1) {
            info.recordState = Constant.RECORD_STOP
        } else if (recordString[0].toInt() == 0 && recordString[1].toInt() > 0) {
            info.recordState = Constant.RECORD_RECORDING
        } else if (recordString[0].toInt() > 0 && recordString[1].toInt() == -1) {
            info.recordState = Constant.RECORD_PAUSE
        } else if (recordString[0].toInt() > 0 && recordString[1].toInt() > 0) {
            info.recordState = Constant.RECORD_RECORDING
        }
        info.isLiving = liveStateStr != "-1"
        if (interacStateStr.toLowerCase() == "conference") {
            info.interaMode = Constant.INTERAC_MODE_CONFERENCE
        } else if (interacStateStr.toLowerCase() == "record") {
            info.interaMode = Constant.INTERAC_MODE_RECORD
        } else if (interacStateStr.toLowerCase() == "teach") {
            info.interaMode = Constant.INTERAC_MODE_TEACH
        } else if (interacStateStr.toLowerCase() == "listen") {
            info.interaMode = Constant.INTERAC_MODE_LISTEN
        }
        if (!TextUtils.isEmpty(meetingInfoStr)) {
            val meeting =
                meetingInfoStr.split(" ".toRegex()).toTypedArray()
            info.meetingNumber = meeting[0]
            info.meetingPassword = meeting[1]
            info.meetingTheme = URLHexEncodeDecodeUtil.hexToStringUrlDecode(meeting[2])
        }
        if (TextUtils.isEmpty(maxCountStr)) {
            info.maxLinkCount = 0
        } else {
            info.maxLinkCount = if (TextUtils.isDigitsOnly(maxCountStr)) maxCountStr.toInt() else 0
        }

        val masterChannel = VolumeChannel()
        val extvol_i = split[7]
        val extvoliSplit = extvol_i.split(",")
        extvoliSplit.forEach {
            val split1 = it.split("=")
            if ("MASTER" == split1[0]) {
                masterChannel.channelName = "MASTER"
                masterChannel.volumnLevel = split1[1].toInt()
            }
        }
        val isDualStream ="1"== split[8]
        info.isDualStream = isDualStream
        info.mainVolumeChannel = masterChannel
        info.extVolumeInfo = extvol_i
        return info
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}