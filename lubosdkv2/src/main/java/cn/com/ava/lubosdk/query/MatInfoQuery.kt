package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.MatInfo
import cn.com.ava.lubosdk.entity.QueryResult
/**
 * 抠像信息
 * */
class MatInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IQuery<MatInfo> {

    override val name: String
        get() = "MatInfo"

    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.UI_KEYINGENABLE,AVATable.GUI_KEYINGENABLE,AVATable.UI_MACHINEMODEL,
        AVATable.UI_KEYINGSDI2)

    override var offsets: Int = 0
        get() = field
        set(value) {field = value}

    override fun build(split: List<String>): MatInfo {
        val info = MatInfo()
        val canMat1 = !TextUtils.isEmpty(split[0]) && split[0] == "enable"
        val canMat2 = canMat1 && split[2].contains("x8s") || split[2]
            .contains("u8") && split[3] == "enable"
        info.isCanMatImage1 = canMat1
        info.isCanMatImage2 = canMat2
        if (!TextUtils.isEmpty(split[1])) {
            val s = split[1].split(" ".toRegex()).toTypedArray()
            val isMatting1 = s[0] == "0" && s[1] == "enable"
            val isMatting2 = s[0] == "1" && s[1] == "enable"
            if (isMatting1) {
                info.curMat = 1
            } else if (isMatting2) {
                info.curMat = 2
            } else {
                info.curMat = 0
            }
        } else {
            val isMatting1 = false
            val isMatting2 = false
            info.curMat = 0
        }
        return info
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}