package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.SimpleWrapper


/**
 * 我的互动号码
 * */
class MyInteractionNumberQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<SimpleWrapper<Int>> {

    override val name: String
        get() = "MyInteractionNumber"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.INTERA_LOCAL_NUMBERID)
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): SimpleWrapper<Int> {
        val s = split[0]
        return if (!TextUtils.isEmpty(s)) {
            SimpleWrapper(s.toInt())
        } else SimpleWrapper(-1)
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}