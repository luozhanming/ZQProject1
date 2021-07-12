package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.SimpleWrapper
/**
 * 能否创建会议
 * */
class CanCreateMeetingQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<SimpleWrapper<Boolean>> {


    override val name: String
        get() = "CanCreateMeeting"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.INTERA_REGSTATE,AVATable.SEM_INTERNALCLOUD,AVATable.RSC_LOGINIDSTUN)
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): SimpleWrapper<Boolean> {
        val loginStun = split[2]
        val split1 = loginStun.split(",".toRegex())
        var hasUsr = false
        for (s in split1) {
            val split2 = s.split("=".toRegex()).toTypedArray()
            if (split2[0] == "usr") {
                hasUsr = !TextUtils.isEmpty(split2[1]) && !split2[1].contains("null")
                break
            }
        }
        return if (split[0].contains("connected") && split[0] != "disconnected") SimpleWrapper(true) else SimpleWrapper(split[1] == "1" && hasUsr)
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}