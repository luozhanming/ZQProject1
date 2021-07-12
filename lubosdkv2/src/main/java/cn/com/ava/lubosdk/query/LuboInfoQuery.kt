package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.LuBoInfo
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil

/**
 * 录播信息
 * */
class LuboInfoQuery(
        override var onResult: (QueryResult) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null
) : IQuery<LuBoInfo> {
    override val name: String
        get() = "LuBoInfo"
    override val mQueryFields: Array<Int>
        get() = arrayOf(
                AVATable.UI_DEVMODEL,
                AVATable.UI_MACHINEMODEL,
                AVATable.UI_VERSION,
                AVATable.RSC_LOGINIDSTUN,
                AVATable.INTERA_REGSTATE,
                AVATable.UI_NEUTRAL,
                AVATable.UI_INTERACT1V3,
                AVATable.UI_LAYOUTSELECT,
                AVATable.BLEND_MODE,
                AVATable.UI_INTERAENABLE,
                AVATable.RSC_SNUMBER,
                AVATable.HTTP_HOSTADDR,
                AVATable.SEM_INTERAMODE,
                AVATable.REC_INFO,
                AVATable.RSC_MTU
        )
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): LuBoInfo {
        val info = LuBoInfo()
        info.devModel = split.get(0)
        info.machineModel = split.get(1)
        var version = 0
        if (split.get(0).contains("_")) {
            val versionStr: String =
                    split.get(2).split("_".toRegex()).toTypedArray().get(0).replace(".", "")
                            .replace("V", "").replace("B", "")
            if (TextUtils.isDigitsOnly(versionStr)) {
                version = versionStr.toInt()
            }
        } else {
            val versionStr: String =
                    split.get(2).replace(".", "").replace("V", "").replace("B", "")
            if (TextUtils.isDigitsOnly(versionStr)) {
                version = versionStr.toInt()
            }
        }
        info.version = version
        info.versionStr = split.get(2)
        info.isNetural = "1" == split.get(5)
        val loginStun: String = split.get(3)
        val split1 = loginStun.split(",".toRegex()).toTypedArray()
        val result = LuBoInfo.LoginIdStun()
        for (s in split1) {
            val split2 = s.split("=".toRegex()).toTypedArray()
            if (split2.size == 1 || split2[1] == "(null)") continue
            if ("usr" == split2[0]) {
                result.setUsr(split2[1])
            } else if ("pwd" == split2[0]) {
                result.setPwd(split2[1])
            } else if ("ip" == split2[0]) {
                result.setIp(split2[1])
            } else if ("localport" == split2[0]) {
                result.setLocalport(split2[1])
            } else if ("globalport" == split2[0]) {
                result.setGlobalport(split2[1])
            } else if ("backupip" == split2[0]) {
                result.setBackupip(split2[1])
            } else if ("globaludpport" == split2[0]) {
                result.setUdpport(split2[1])
            } else if ("namec" == split2[0]) {
                result.setNickname(URLHexEncodeDecodeUtil.hexToStringUrlDecode(split2[1]))
            }
        }
        val split2: Array<String> = split.get(4).split(" ".toRegex()).toTypedArray()
        result.setConnectState(split2[0])
        result.setShortnum(split.get(10))
        info.stun = result
        info.ip = split.get(11).split(":".toRegex()).toTypedArray().get(0)
        //能否1V3解析
        //能否1V3解析
        val machineModel: String = split.get(1)
        val devModel: String = split.get(0)
        val interac1V3: String = split.get(6)
        if (machineModel.contains("h2") || machineModel.contains("x8s") || machineModel.contains("h4m")) {
            if (devModel.contains("AE-A") || devModel.contains("AE-E") || devModel.contains("AE-C") || devModel.contains(
                            "AE-V"
                    )
            ) {
                info.isCanInterac1V3 = !devModel.contains("S")
            }
        } else {
            info.isCanInterac1V3 = interac1V3 == "2" || machineModel.contains("ql")
        }
        //解析电脑画面索引
        //解析电脑画面索引
        val split3: Array<String> = split.get(7).split(",".toRegex()).toTypedArray()
        for (s in split3) {
            val split4 = s.split("/".toRegex()).toTypedArray()
            if (split4[1].contains("_")) {
                val s1 =
                        split4[1].split("_".toRegex()).toTypedArray()
                info.computerIndex = s1[0]
                break
            }
        }
        info.isCanInterac = "enable" == split.get(9)
        if (TextUtils.isEmpty(split.get(12)) || Constant.INTERAC_MODE_RECORD.equals(split.get(12))) {
            info.interacMode = Constant.INTERAC_MODE_RECORD
        } else {
            info.interacMode = split.get(12)
        }
        if (TextUtils.isEmpty(split.get(13))) {
            info.recordState = Constant.RECORD_STOP
        } else {
            val recordString: Array<String> =
                    split.get(13).split(" ".toRegex()).toTypedArray()
            if (recordString[0].toInt() == 0 && recordString[1].toInt() == -1) {
                info.recordState = Constant.RECORD_STOP
            } else if (recordString[0].toInt() == 0 && recordString[1].toInt() > 0) {
                info.recordState = Constant.RECORD_RECORDING
            } else if (recordString[0].toInt() > 0 && recordString[1].toInt() == -1) {
                info.recordState = Constant.RECORD_PAUSE
            } else if (recordString[0].toInt() > 0 && recordString[1].toInt() > 0) {
                info.recordState = Constant.RECORD_RECORDING
            }
        }
        //缓存一下
        val mtu = split[14].toIntOrNull()
        info.mtu = mtu?:1500
        Cache.getCache().setLuBoInfo(info)
        return info
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {
            field = value
        }
}