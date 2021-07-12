package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.ListenerInfo
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.VolumeChannel
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
import java.util.*
/**
 * 互动听课信息
 * */
class ListenerInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<ListenerInfo> {

    override val name: String
        get() = "ListenerInfo"

    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.AT_AUTOTRACK, AVATable.REC_INFO,
            AVATable.RTSP_INFO, AVATable.SEM_INTERAMODE,
            AVATable.INTERA_MEETINGINFO, AVATable.RSC_SETMAXSHOWNUM,
            AVATable.UI_OUTPUTAUDIO,AVATable.AB_VOLUME_I,
            AVATable.SEM_DUALSTREAMMODE,AVATable.SEM_DUALSTREAMENABLE,
            AVATable.SEM_REQUESTDUALSTREAM,AVATable.SEM_SETMUTE,
            AVATable.SEM_REQUESTUNMUTE,AVATable.UI_INTERASOURCE,
            AVATable.UI_INTERAHDMI,AVATable.SEM_INTERAHDMISOURCE,
            AVATable.SEM_INTERAMAINSTREAM,AVATable.SEM_INTERNALCLOUD,
            AVATable.SEM_INTERAPROTOCOL,AVATable.SEM_SHAREDUALSTREAM,
            AVATable.UI_KEYINGENABLE,AVATable.GUI_KEYINGENABLE,
            AVATable.UI_MACHINEMODEL,AVATable.UI_KEYINGSDI2,
            AVATable.SEM_LOCALROLE,AVATable.UI_KEYWINDOWLOCATE
        )

    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): ListenerInfo {
        val info = ListenerInfo()
        val autoTrackStr: String = split.get(0)
        val recordStateStr: String = split.get(1)
        val liveStateStr: String = split.get(2)
        val interacStateStr: String = split.get(3)
        val meetingInfoStr: String = split.get(4)
        val maxCountStr: String = split.get(5)
        val meetingInfo = "{" + split.get(6) + "}"
        info.setTrackMode(autoTrackStr)
        info.setInternalCloud(split.get(17) == "1")
        if (TextUtils.isEmpty(split.get(18))) {
            info.setProtocol(0)
        } else {
            info.setProtocol(split.get(18).toInt())
        }
        val recordString =
            recordStateStr.split(" ".toRegex()).toTypedArray()
        if (recordString[0].toInt() == 0 && recordString[1].toInt() == -1) {
            info.setRecordState(Constant.RECORD_STOP)
        } else if (recordString[0].toInt() == 0 && recordString[1].toInt() > 0) {
            info.setRecordState(Constant.RECORD_RECORDING)
        } else if (recordString[0].toInt() > 0 && recordString[1].toInt() == -1) {
            info.setRecordState(Constant.RECORD_PAUSE)
        } else if (recordString[0].toInt() > 0 && recordString[1].toInt() > 0) {
            info.setRecordState(Constant.RECORD_RECORDING)
        }
        info.setLiving(liveStateStr != "-1")
        if (interacStateStr.toLowerCase() == "conference") {
            info.setInteraMode(Constant.INTERAC_MODE_CONFERENCE)
        } else if (interacStateStr.toLowerCase() == "record") {
            info.setInteraMode(Constant.INTERAC_MODE_RECORD)
        } else if (interacStateStr.toLowerCase() == "teach") {
            info.setInteraMode(Constant.INTERAC_MODE_TEACH)
        } else if (interacStateStr.toLowerCase() == "class") {
            info.setInteraMode(Constant.INTERAC_MODE_LISTEN)
        }
        if (!TextUtils.isEmpty(meetingInfoStr)) {
            val meeting =
                meetingInfoStr.split(" ".toRegex()).toTypedArray()
            info.setMeetingNumber(meeting[0])
            info.setMeetingPassword(meeting[1])
            info.setMeetingTheme(URLHexEncodeDecodeUtil.hexToStringUrlDecode(meeting[2]))
        }
        info.setMaxLinkCount(
            if (!TextUtils.isEmpty(maxCountStr) && TextUtils.isDigitsOnly(
                    maxCountStr
                )
            ) maxCountStr.toInt() else 0
        )
        val mainOutputName: String = split.get(6)
        val mainOutputValue: String = split.get(7)
        val masterChannel = VolumeChannel()
        if (TextUtils.isEmpty(mainOutputName)) { //缺省默认名
            masterChannel.setChannelName("MASTER")
        } else {
            masterChannel.setChannelName(mainOutputName)
        }
        if (TextUtils.isEmpty(mainOutputValue)) {
            masterChannel.setVolumnLevel(0)
        } else {
            val split1 =
                mainOutputValue.split(",".toRegex()).toTypedArray()
            for (s in split1) {
                val split2 = s.split("=".toRegex()).toTypedArray()
                if (split2[0] == mainOutputName) {
                    masterChannel.setVolumnLevel(split2[1].toInt())
                }
            }
        }
        info.setMainVolumeChannel(masterChannel)
        val s: String = split.get(8)
        val enableDualStream = s == "1"
        info.setShareDualStream(enableDualStream)
        val s1: String = split.get(9)
        if (!TextUtils.isEmpty(s1)) {
            info.setCurShareDualStreamUser(s1.toInt())
        }
        //当前申请双流
        //当前申请双流
        val s2: String = split.get(10)
        val list: MutableList<Int> = ArrayList()
        val split1 = s2.split(",".toRegex()).toTypedArray()
        for (s3 in split1) {
            if (!TextUtils.isEmpty(s3) && TextUtils.isDigitsOnly(s3)) {
                list.add(s3.toInt())
            }
        }
        info.setCurRequestShareStreamList(list)
        //发言解析
        //发言解析
        val list1: MutableList<Int> = ArrayList()
        val list2: MutableList<Int> = ArrayList()
        val s3: String = split.get(11)
        val split2 = s3.split(",".toRegex()).toTypedArray()
        for (s4 in split2) {
            if (!TextUtils.isEmpty(s4) && TextUtils.isDigitsOnly(s4)) {
                list1.add(s4.toInt())
            }
        }
        info.setCurSpeakingList(list1)
        //当前申请发言解析
        val s5: String = split.get(12)
        val split3 = s5.split(",".toRegex()).toTypedArray()
        for (s4 in split3) {
            if (!TextUtils.isEmpty(s4) && TextUtils.isDigitsOnly(s4)) {
                list2.add(s4.toInt())
            }
        }
        info.setCurRequestSpeakList(list2)
        //解析主辅流窗口
        //解析主辅流窗口
        val windows: List<PreviewVideoWindow> =
            Cache.getCache().getWindowsCache()
        if (windows != null) {
            val mainInt: MutableList<Int> = ArrayList()
            val subInt: MutableList<Int> = ArrayList()
            val mainWindows: MutableList<PreviewVideoWindow> =
                ArrayList<PreviewVideoWindow>()
            val subWindows: MutableList<PreviewVideoWindow> =
                ArrayList<PreviewVideoWindow>()
            val mainStreamRes: String = split.get(13)
            val subStreamRes: String = split.get(14)
            var curSub = -1
            var curMain = -1
            if (!TextUtils.isEmpty(split.get(15))) {
                curSub = split.get(15).toInt()
            }
            if (!TextUtils.isEmpty(split.get(16))) {
                curMain = split.get(16).toInt()
            }
            val split4 =
                mainStreamRes.split("/".toRegex()).toTypedArray()
            if (split4.size > 0) {
                val s4 = split4[0]
                val split5 = s4.split(",".toRegex()).toTypedArray()
                for (s6 in split5) {
                    if (TextUtils.isEmpty(s6)) continue
                    mainInt.add(s6.toInt())
                }
            }
            if (split4.size > 1) {
                val s4 = split4[1]
                val split5 = s4.split(",".toRegex()).toTypedArray()
                for (s6 in split5) {
                    mainInt.add(s6.toInt())
                }
            }
            val split5 =
                subStreamRes.split(",".toRegex()).toTypedArray()
            for (s4 in split5) {
                subInt.add(s4.toInt())
            }
            for (previewWindow in windows) {
                val index: Int = previewWindow.getIndex()
                previewWindow.setCurMain(curMain == index)
                previewWindow.setCurSub(curSub == index)
                if (mainInt.contains(index)) {
                    mainWindows.add(previewWindow)
                }
                if (subInt.contains(index)) {
                    subWindows.add(previewWindow)
                }
            }
            info.setMainStreamWindows(mainWindows)
            info.setSubStreamWindows(subWindows)
            info.setSipShareDocStream("1" == split.get(19))
        }
        val matState: Array<String> =
            split.get(21).split(" ".toRegex()).toTypedArray()
        var canMat1 =
            !TextUtils.isEmpty(split.get(20)) && split.get(20) == "enable"
        var canMat2 =
            canMat1 && split.get(22).contains("x8s") || split.get(22).contains("u8") && split.get(
                23
            ) == "enable"
        var isMatting1 = matState[0] == "0" && matState[1] == "enable"
        var isMatting2 = matState[0] == "1" && matState[1] == "enable"
        val keylocateString: String = split.get(25)
        val keylocate = intArrayOf(-1, -1)
        if (!TextUtils.isEmpty(keylocateString) && keylocateString.contains("/")) {
            val keylocateSplit =
                keylocateString.split("/".toRegex()).toTypedArray()
            val locate1 = keylocateSplit[0]
            val locate2 = keylocateSplit[1]
            if (!TextUtils.isEmpty(locate1)) {
                keylocate[0] = locate1.toInt()
            }
            if (!TextUtils.isEmpty(locate2)) {
                keylocate[1] = locate2.toInt()
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
        info.setPanTing("3" == split.get(24))
        return info
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}