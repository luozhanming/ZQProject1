package cn.com.ava.lubosdk.query

import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.ComputerIndex
import cn.com.ava.lubosdk.entity.QueryResult
/**
 * 查询录播定义为电脑的窗口索引
 * */
class ComputerIndexQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<ComputerIndex> {

    override val name: String
        get() = "ComputerIndex"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.UI_LAYOUTSELECT, AVATable.BLEND_MODE,AVATable.AT_COMPUTERMODE)
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): ComputerIndex {
        val split1: Array<String> = split.get(0).split(",".toRegex()).toTypedArray()
        for (s in split1) {
            val split2 = s.split("/".toRegex()).toTypedArray()
            if (split2[1].contains("_")) {
                val s1 =
                    split2[1].split("_".toRegex()).toTypedArray()
                return ComputerIndex(s1[0])
            }
        }
        return ComputerIndex(split[2])
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}