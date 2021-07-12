package cn.com.ava.lubosdk.query

import android.text.TextUtils
import android.util.SparseArray
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.RemoteVideoEnable


/**
 * 海南互动远端视频源信息
 * */
class RemoteVideoEnableQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IQuery<RemoteVideoEnable> {


    override val name: String
        get() = "RemoteVideoEnable"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.RSC_SETMAXSHOWNUM,AVATable.INTERA_REMOTEVIDEOSWITCH,
                AVATable.UI_DEVMODEL, AVATable.UI_MACHINEMODEL,
                AVATable.UI_INTERACT1V3,AVATable.SEM_TEACHMODE,
                AVATable.INTERA_MEETINGINFO)
    override var offsets: Int = 0
        get() = field
        set(value) {field = value}

    private var canInterac1V3:Boolean = false


    override fun build(split: List<String>): RemoteVideoEnable {
        //能否1V3解析
        val machineModel = split[3]
        val devModel = split[2]
        val interac1V3 = split[4]
        if (machineModel.contains("h2") || machineModel.contains("x8s") || machineModel.contains("h4m")) {
            if (devModel.contains("AE-A") || devModel.contains("AE-E") || devModel.contains("AE-C") || devModel.contains(
                            "AE-V"
                    )
            ) {
                canInterac1V3 = !devModel.contains("S")
            }
        } else {
            canInterac1V3 = interac1V3 == "2" || machineModel.contains("ql")
        }

        val showNum = parseTeachMode(split[5],split[4],split[6])
        val maxShow =showNum-1
        val enable = RemoteVideoEnable()
        if (!TextUtils.isEmpty(split[1])) {
            val enables: SparseArray<Boolean> = SparseArray(3)
            val s = split[1].split(",".toRegex()).toTypedArray()
            for (i in s.indices) {
                enables.put(i + 1, "1" == s[i])
            }
            enable.canControlRemotes = enables
        }
        enable.maxRemote = maxShow
        return enable
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}

    private fun parseTeachMode(
            teachMode: String,
            interac1V3: String,
            meetingInfo: String
    ): Int {
        var `val` = 0
        if ("" != teachMode) {
            `val` = teachMode.toInt()
        }
        val canInterac1V3 = canInterac1V3
        var isInnerCloud = false
        val s = meetingInfo.split(" ".toRegex()).toTypedArray()
        isInnerCloud = s.isNotEmpty() && s[0].length == 9
        return if (!canInterac1V3 && isInnerCloud) {
            2
        } else when (`val`) {
            0 -> 4
            1 -> 3
            2 -> 6
            else -> 4
        }
    }
}