package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.SimpleWrapper

/**
 * 查询当前是否内置云
 * */
class InnerCloudQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<SimpleWrapper<Boolean>> {


    override val name: String
        get() = "IsInnerCloud"


    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.INTERA_MEETINGINFO
        )

    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): SimpleWrapper<Boolean> {
        return if (TextUtils.isEmpty(split.get(0))) {
            SimpleWrapper(false)
        } else {
            val s: Array<String> = split.get(0).split(" ".toRegex()).toTypedArray()
            SimpleWrapper(s.isNotEmpty() && s[0].length == 9)
        }
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}


}