package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.SimpleWrapper

/**
 * 查询是否sip互动
 * */
class IsSipInteractionQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<SimpleWrapper<Boolean>> {
    override val name: String
        get() = "isSipInteraction"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.SEM_INTERAMODE, AVATable.INTERA_MEETINGINFO)
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): SimpleWrapper<Boolean> {
        return SimpleWrapper(
                (Constant.INTERAC_MODE_TEACH == split[0] || Constant.INTERAC_MODE_LISTEN == split[0]) && TextUtils.isEmpty(split[1])
        )
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}