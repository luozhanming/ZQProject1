package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.TsInteracSource
import java.util.*

class TsInteracSourceQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IQuery<ListWrapper<TsInteracSource>> {

    override val name: String
        get() = "TsInteracSource"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.UI_INTERASOURCE,AVATable.SEM_INTERATSSOURCE)
    override var offsets: Int = 0
        get() = field
        set(value) {field = value}

    override fun build(split: List<String>): ListWrapper<TsInteracSource> {
        val sources: MutableList<TsInteracSource> =
            ArrayList()
        val s = split[0].split("/".toRegex()).toTypedArray()
        if (s.size > 0 && !TextUtils.isEmpty(s.get(0))) { //有老师
            val source = TsInteracSource()
            source.type = Constant.TYPE_TEACHER
            val split1: Array<String> = s.get(0).split(",".toRegex()).toTypedArray()
            val names: MutableList<String> =
                ArrayList()
            val indexs: MutableList<Int> = ArrayList()
            for (s1 in split1) {
                names.add(Cache.getCache().getWindowsByIndex(s1.toInt()).windowName)
                indexs.add(s1.toInt())
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
            sources.add(source)
        }
        if (s.size > 1 && !TextUtils.isEmpty(s.get(1))) {   //学生
            val source = TsInteracSource()
            source.type = Constant.TYPE_STUDENT
            val split1: Array<String> = s.get(1).split(",".toRegex()).toTypedArray()
            val names: MutableList<String> =
                ArrayList()
            val indexs: MutableList<Int> = ArrayList()
            for (s1 in split1) {
                names.add(Cache.getCache().getWindowsByIndex(s1.toInt()).getWindowName())
                indexs.add(s1.toInt())
            }
            if (TextUtils.isEmpty(split.get(1))) {   //空的
                source.selectIndex = 0
            } else if (split.get(1).split(" ".toRegex()).toTypedArray().size > 1) {  //不空
                val s1: Array<String> =
                    split.get(1).split(" ".toRegex()).toTypedArray()
                val i = s1[1].toInt()
                source.selectIndex = indexs.indexOf(i)
            } else if (split.get(1).split(" ".toRegex()).toTypedArray().size > 0) {  //不空
                val s1: Array<String> =
                    split.get(1).split(" ".toRegex()).toTypedArray()
                val i = s1[0].toInt()
                source.selectIndex = indexs.indexOf(i)
            }
            source.windowIndex = indexs
            source.windowNames = names
            sources.add(source)
        }
        return ListWrapper(sources)
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}