package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.SimpleWrapper


/**
 * 查询是否需要编码加入会议密码
 * */
class IsEncodeJoinMeetingPswQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IQuery<SimpleWrapper<Boolean>> {
    override val name: String
        get() = "IsEncodeJoinMeetingPsw"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.UI_VERSION,AVATable.UI_MACHINEMODEL)
    override var offsets: Int = 0
        get() = field
        set(value) {field = value}

    override fun build(split: List<String>): SimpleWrapper<Boolean> {
        var version = 0
        if (split.get(0).contains("_")) {
            val versionStr: String =
                split.get(0).split("_".toRegex()).toTypedArray().get(0).replace(".", "")
                    .replace("V", "").replace("B", "")
            if (TextUtils.isDigitsOnly(versionStr)) {
                version = versionStr.toInt()
            }
        } else {
            val versionStr: String =
                split.get(0).replace(".", "").replace("V", "").replace("B", "")
            if (TextUtils.isDigitsOnly(versionStr)) {
                version = versionStr.toInt()
            }
        }
        val machineModel: String = split.get(1)
        return if (machineModel == "h2") {
            SimpleWrapper(version >= 1146)
        } else if (machineModel == "x8s") {
            SimpleWrapper(version >= 8648)
        } else if (machineModel == "u8") {
            SimpleWrapper(version >= 1135)
        } else if (machineModel == "u2") {
            SimpleWrapper(version >= 1110)
        } else if ("u8a" == machineModel) {
            SimpleWrapper(true)
        } else SimpleWrapper(true)
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}

}