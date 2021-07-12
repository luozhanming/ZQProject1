package cn.com.ava.lubosdk.query

import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.LayoutButtonInfo
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.QueryResult
/**
 * 录播画面布局信息
 * */
class LayoutButtonInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)?=null
) :IQuery<ListWrapper<LayoutButtonInfo>> {


    override val name: String
        get() = "LayoutButtonInfo"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.UI_LAYOUTSELECT,AVATable.BLEND_MODE)
    override var offsets: Int = 0
        get() = field
        set(value) {field = value}

    override fun build(split: List<String>): ListWrapper<LayoutButtonInfo> {
        val curLayout: String = split.get(1)
        val split1: Array<String> = split.get(0).split(",".toRegex()).toTypedArray()
        val cmds: MutableList<LayoutButtonInfo> =
            mutableListOf()
        for (s in split1) {
            val info = LayoutButtonInfo()
            val split2 = s.split("/".toRegex()).toTypedArray()
            info.cmd = split2[1]
            info.isCurLayout = split2[1] == curLayout
            cmds.add(info)
        }
        return ListWrapper(cmds)
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}