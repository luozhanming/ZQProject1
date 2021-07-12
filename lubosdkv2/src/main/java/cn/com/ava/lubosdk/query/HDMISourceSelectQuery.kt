package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.TsInteracSource
import java.util.*


/**
 * 查询HDMI的子信号源（必须在PreviewWindow请求之后）
 * */
class HDMISourceSelectQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IQuery<TsInteracSource> {
    override val name: String
        get() = "HDMISourceSelect"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.UI_INTERAHDMI,AVATable.SEM_INTERAHDMISOURCE)
    override var offsets: Int = 0
        get() =field
        set(value) {field = value}

    override fun build(split: List<String>): TsInteracSource {
        val source = TsInteracSource()
        val hdmiIndexStr: String = split.get(0)
        var hdmiIndex: Array<String>? = null
        if (!TextUtils.isEmpty(hdmiIndexStr)) {
            hdmiIndex = hdmiIndexStr.split(",".toRegex()).toTypedArray()
            source.type = Constant.TYPE_HDMI
            val names: MutableList<String> =
                ArrayList()
            val indexs: MutableList<Int> = ArrayList()
            for (index in hdmiIndex) {
                names.add(Cache.getCache().getWindowsByIndex(index.toInt()).getWindowName())
                indexs.add(index.toInt())
            }
            if (TextUtils.isEmpty(split.get(1))) {   //空的
                source.selectIndex = 0
            } else {  //不空
                val s1: Array<String> =
                    split.get(1).split(" ".toRegex()).toTypedArray()
                val i = s1[0].toInt()
                source.selectIndex = indexs.indexOf(i)
            }
            source.windowIndex = indexs
            source.windowNames = names
        }
        return source
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}