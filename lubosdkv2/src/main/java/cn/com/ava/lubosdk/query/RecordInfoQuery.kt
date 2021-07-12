package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.RecordInfo
import cn.com.ava.lubosdk.util.LuboUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
/**
 * 录课模式信息
 * */
class RecordInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<RecordInfo> {
    override val name: String
        get() = "RecordInfo"
    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.AT_AUTOTRACK, AVATable.REC_INFO,
            AVATable.RTSP_INFO, AVATable.SEM_INTERAMODE,
            AVATable.BLEND_MODE, AVATable.REC_FREESPACE,
            AVATable.PROGRAM_INFO1, AVATable.PROGRAM_INFO2,
            AVATable.LAYOUT_MONITORROWS, AVATable.LAYOUT_MONITORVIDEOS,
            AVATable.UI_KEYINGENABLE, AVATable.GUI_KEYINGENABLE,
            AVATable.UI_MACHINEMODEL, AVATable.UI_KEYINGSDI2,
            AVATable.UI_KEYWINDOWLOCATE
        )
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): RecordInfo {
        val info = RecordInfo()
        info.setTrackMode(split[0])
        val recordString =
            split[1].split(" ".toRegex()).toTypedArray()
        if (recordString[0].toInt() == 0 && recordString[1].toInt() == -1) {
            info.setRecordState(Constant.RECORD_STOP)
        } else if (recordString[0].toInt() == 0 && recordString[1].toInt() > 0) {
            info.setRecordState(Constant.RECORD_RECORDING)
        } else if (recordString[0].toInt() > 0 && recordString[1].toInt() == -1) {
            info.setRecordState(Constant.RECORD_PAUSE)
        } else if (recordString[0].toInt() > 0 && recordString[1].toInt() > 0) {
            info.setRecordState(Constant.RECORD_RECORDING)
        }
        info.setLiving(split[2] != "-1")
        if (TextUtils.isEmpty(split[3])) {
            info.setInteraState(Constant.INTERAC_MODE_RECORD)
        } else {
            info.setInteraState(split[3])
        }
        info.setCurrentOutputWindow(split[4])
        val freeSpace = split[5]
        val freeSpaceSplit =
            freeSpace.split(",".toRegex()).toTypedArray()
        val systemTime = split[15].toLong()
        info.setFileSize(freeSpaceSplit[1].substring(freeSpaceSplit[1].indexOf("=") + 1))
        info.setClassTheme(URLHexEncodeDecodeUtil.hexToStringUrlDecode(split[6], "GBK"))
        info.setTeacher(URLHexEncodeDecodeUtil.hexToStringUrlDecode(split[7], "GBK"))
        info.setRecordTime(LuboUtil.computeRecordTime(systemTime, split[1]))
        info.setPreviewRow(split[8].toInt())
        info.setPreviewLie(split[9].toInt())
        val matState =
            split[11].split(" ".toRegex()).toTypedArray()
        var canMat1 =
            !TextUtils.isEmpty(split[10]) && split[10] == "enable"
        var canMat2 =
            canMat1 && split[12].contains("x8s") || split[12].contains("u8") && split[13] == "enable"
        var isMatting1 = matState[0] == "0" && matState[1] == "enable"
        var isMatting2 = matState[0] == "1" && matState[1] == "enable"
        val keylocateString = split[14]
        val keylocate = intArrayOf(-1, -1)
        if (!TextUtils.isEmpty(keylocateString) && keylocateString.contains("/")) {
            val keylocateSplit =
                keylocateString.split("/".toRegex()).toTypedArray()
            val s1 = keylocateSplit[0]
            val s2 = keylocateSplit[1]
            if (!TextUtils.isEmpty(s1)) {
                keylocate[0] = s1.toInt()
            }
            if (!TextUtils.isEmpty(s2)) {
                keylocate[1] = s2.toInt()
            }
        } else {
            keylocate[0] = -1
            keylocate[1] = -1
        }
        if (keylocate[0] > keylocate[1]) {
            val tempCan = canMat1
            canMat1 = canMat2
            canMat2 = tempCan
            val tempIs = isMatting1
            isMatting1 = isMatting2
            isMatting2 = tempIs
        }
        info.setMatEnable(canMat1)
        info.setMat2Enable(canMat2)
        info.setMatOpen(isMatting1)
        info.setMat2Open(isMatting2)
        return info
    }

    override fun isNeedSystemTime(): Boolean = true
    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}