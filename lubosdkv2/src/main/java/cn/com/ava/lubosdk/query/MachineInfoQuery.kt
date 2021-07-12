package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.MachineInfo
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.RecordParams
import cn.com.ava.lubosdk.entity.StoreSpace
/**
 * 设备信息
 * */
class MachineInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)?=null
) :IQuery<MachineInfo> {


    override val name: String
        get() = "MachineInfo"

    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.RTSP_PARAM1,AVATable.REC_FREESPACE,AVATable.FTPC_STATUS,AVATable.CPU_TMP)

    override var offsets: Int = 0
        get() = field
        set(value) {field = value}

    override fun build(split: List<String>): MachineInfo {
        val info = MachineInfo()
        val recParams = split[0]
        val rp = RecordParams()
        val recParamsSplit =
            recParams.split(",".toRegex()).toTypedArray()
        for (s in recParamsSplit) {
            val split1 = s.split("=".toRegex()).toTypedArray()
            if (split1[0] == "chn") {
                rp.setChannel(split1[1].toInt())
            } else if (split1[0] == "width") {
                rp.setWidth(split1[1].toInt())
            } else if (split1[0] == "height") {
                rp.setHeight(split1[1].toInt())
            } else if (split1[0] == "bps") {
                rp.setBps(split1[1].toInt())
            } else if (split1[0] == "fps") {
                rp.setFramePerSecond(split1[1].toInt())
            } else if (split1[0] == "mode") {
                rp.setMode(split1[1].toInt())
            } else if (split1[0] == "gop") {
                rp.setGop(split1[1].toInt())
            }
        }
        info.recordParams = rp
        val space = split[1]
        val spaceSplit = space.split(",".toRegex()).toTypedArray()
        val ss = StoreSpace()
        for (s in spaceSplit) {
            val split1 = s.split("=".toRegex()).toTypedArray()
            if (split1[0] == "free") {
                ss.setFreeSize(split1[1])
            } else if (split1[0] == "nowsize") {
                ss.setNowSize(split1[1])
            } else if (split1[0] == "total") {
                ss.setTotalSize(split1[1])
            }
        }
        info.remainSpace = ss
        val tmp = split[3]
        val temp = String.format("%.2f℃", tmp.toFloat())
        info.cpuTemperature = temp
        var ftpMsg = split[2]
        if (TextUtils.isEmpty(ftpMsg)) {
            ftpMsg = "无任务"
        } else {
            if (ftpMsg.contains("Uploading")) {
                ftpMsg = "上传中"
            } else if (ftpMsg.contains("NoMission")) {
                ftpMsg = "无任务"
            } else if (ftpMsg.contains("Connect")) {
                ftpMsg = "连接失败，请检查ip和端口"
            } else if (ftpMsg.contains("Login")) {
                ftpMsg = "登录失败，检查用户名密码"
            } else if (ftpMsg.contains("ParamError")) {
            } else if (ftpMsg.contains("Failed")) {
                ftpMsg = "上传失败"
            } else if (ftpMsg.contains("Done")) {
                ftpMsg = "上传完成"
            } else if (ftpMsg.contains("Repeat")) {
            } else if (ftpMsg.contains("Full")) {
            } else if (ftpMsg.contains("UploadStart")) {
                ftpMsg = "开始上传"
            }
        }
        info.hasFTPTask = ftpMsg
        return info
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}