package cn.com.ava.lubosdk.query

import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.CurPreviewWindow
import cn.com.ava.lubosdk.entity.QueryResult

/**
 * 当前画面显示的窗口
 * */
class CurPreviewWindowQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<CurPreviewWindow> {


    override val name: String
        get() = "CurPreviewWindow"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.BLEND_MODE)
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): CurPreviewWindow {
        return CurPreviewWindow(split[0])
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}