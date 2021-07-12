package cn.com.ava.lubosdk.query

import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.InteracTeacherModeInfo
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.util.LuboUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil


/**
 * 互动主讲基本信息
 * */
class InteracTeacherModeInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<InteracTeacherModeInfo> {


    override val name: String
        get() = "InteracTeacherModeInfoQuery"


    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.AT_AUTOTRACK, AVATable.REC_INFO,
            AVATable.RTSP_INFO, AVATable.SEM_INTERAMODE,
            AVATable.INTERA_MEETINGINFO
        )


    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): InteracTeacherModeInfo {
        val info = InteracTeacherModeInfo()
        info.trackMode = split.get(0)
        val recordString: Array<String> =
            split.get(1).split(" ".toRegex()).toTypedArray()
        if (recordString[0].toInt() == 0 && recordString[1].toInt() == -1) {
            info.recordState = Constant.RECORD_STOP
        } else if (recordString[0].toInt() == 0 && recordString[1].toInt() > 0) {
            info.recordState = Constant.RECORD_RECORDING
        } else if (recordString[0].toInt() > 0 && recordString[1].toInt() == -1) {
            info.recordState = Constant.RECORD_PAUSE
        } else if (recordString[0].toInt() > 0 && recordString[1].toInt() > 0) {
            info.recordState = Constant.RECORD_RECORDING
        }
        info.isLiving = split.get(2) != "-1"
        info.interaState = split.get(3)
        val meetinginfo: String = split.get(4)
        val s = meetinginfo.split(" ".toRegex()).toTypedArray()
        if (s.size == 3) {
            info.meetingTheme = URLHexEncodeDecodeUtil.hexToStringUrlDecode(s[2], "GBK")
            info.meetingNumber = s[0]
            info.meetingPassword = s[1]
        }
        val recordTime: String =
            LuboUtil.computeRecordTime(split.get(5).toLong(), split.get(1))
        info.recordTime = recordTime
        return info
    }

    override fun isNeedSystemTime(): Boolean = true
    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}