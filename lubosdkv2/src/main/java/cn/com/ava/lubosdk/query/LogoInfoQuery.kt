package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.LogoInfo
import cn.com.ava.lubosdk.entity.QueryResult
/**
 * 画面Logo信息
 * */
class LogoInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IQuery<LogoInfo> {


    override val name: String
        get() = "LogoInfo"

    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.OSD_LOGOVISIBLE,AVATable.OSD_LOGOPOSITION,AVATable.OSD_LOGOSIZE)

    override var offsets: Int = 0
        get() = field
        set(value) {field = value}

    override fun build(split: List<String>): LogoInfo {
        val info = LogoInfo()
        val isVisible = split[0] == "show"
        info.isVisible = isVisible
        val position = split[1].split(" ".toRegex()).toTypedArray()
        val x = if (position.size > 0) position[0].toInt() else 0
        val y = if (position.size > 1) position[1].toInt() else 0
        info.x = x
        info.y = y
        val size = if (split.size > 2) split[2] else ""
        if (TextUtils.isEmpty(size)) {
            info.width = 100
            info.height = 100
        } else {
            val sizeSplit = size.split(" ".toRegex()).toTypedArray()
            info.width = sizeSplit[0].toInt()
            info.height = sizeSplit[1].toInt()
        }
        return info

    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}