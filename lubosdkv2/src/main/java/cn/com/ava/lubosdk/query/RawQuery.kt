package cn.com.ava.lubosdk.query

import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.SimpleWrapper
/**
 * 不解析获取信息
 *
 * */
class RawQuery(
    val queryWhat:Array<Int>,
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<ListWrapper<String>> {
    override val name: String
        get() = "RawQuery"
    override val mQueryFields: Array<Int>
        get() = queryWhat

    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override var isResume: Boolean = false
        get() = field
        set(value) {
            field = value
        }

    override fun build(result: List<String>): ListWrapper<String> {
        return ListWrapper(result)
    }
}