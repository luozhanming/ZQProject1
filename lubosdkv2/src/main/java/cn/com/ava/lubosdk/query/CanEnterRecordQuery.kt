package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.SimpleWrapper


/**
 * 询问录播是否可进入录课
 * */
class CanEnterRecordQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<SimpleWrapper<Boolean>> {
    override val name: String
        get() = "CanEnterRecord"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.SEM_INTERAMODE)
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): SimpleWrapper<Boolean> {
        return SimpleWrapper(split.get(0) == "record" || TextUtils.isEmpty(split.get(0)))
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}

}