package cn.com.ava.lubosdk.query

import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.SimpleWrapper

/**
 * 互动中获取我的互动名
 * */
class MyInteractionNameQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IQuery<SimpleWrapper<String>> {

    override val name: String
        get() = "MyInteractionName"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.RSC_LOGINIDSTUN)
    override var offsets: Int = 0
        get() =field
        set(value) {field = value}

    override fun build(split: List<String>): SimpleWrapper<String> {
        val s: String = split.get(0)
        val split1 = s.split(",".toRegex()).toTypedArray()
        for (s1 in split1) {
            val split2 = s1.split("=".toRegex()).toTypedArray()
            if (split2[0] == "usr") {
                return SimpleWrapper(split2[1])
            }
        }
        return SimpleWrapper("")
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}