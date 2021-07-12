package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.LocalVideoStream
import cn.com.ava.lubosdk.entity.QueryResult


/**
 *必须先请求过PreviewWindow
 * */
class LocalVideoStreamQuery(
    val isMain: Boolean,
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<ListWrapper<LocalVideoStream>> {

    override val name: String
        get() = "LocalVideoStream"

    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.UI_INTERASOURCE,
            AVATable.UI_INTERAHDMI,
            AVATable.SEM_INTERAHDMISOURCE,
            AVATable.SEM_INTERAMAINSTREAM,
            AVATable.SEM_DUALSTREAMMODE
        )

    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): ListWrapper<LocalVideoStream> {
        val streams: MutableList<LocalVideoStream> =
            mutableListOf()
        val split1 = split[0].split("/".toRegex()).toTypedArray()
        val split3 = split[1].split(",".toRegex()).toTypedArray()
        var curSub = 0
        var curMain = 0
        curSub = if (!TextUtils.isEmpty(split[2])) {
            split[2].toInt()
        } else {
            -1
        }
        curMain = if (!TextUtils.isEmpty(split[3])) {
            split[3].toInt()
        } else {
            -1
        }
        //主流解析
        if (isMain) {
            for (s in split1) {
                val split2 = s.split(",".toRegex()).toTypedArray()
                for (s1 in split2) {
                    val stream = LocalVideoStream()
                    if (TextUtils.isEmpty(s1)) continue
                    val i = s1.toInt()
                    stream.windowIndex = i
                    stream.windowName =    Cache.getCache().getWindowsByIndex(i).windowName
                    stream.isMain = true
                    stream.isMainOutput = i == curMain
                    streams.add(stream)
                }
            }
        }
        //辅流解析
        for (s in split3) {
            val stream = LocalVideoStream()
            val i = s.toInt()
            stream.windowIndex = i
            stream.windowName =  Cache.getCache().getWindowsByIndex(i).windowName
            stream.isMain = isMain
            stream.isSubOutput = curSub == i
            stream.isMainOutput = i == curMain
            if (split.size == 1) {
            }
            if (!isMain) {
                stream.isEnableDualStream = split[4] == "1"
            }
            streams.add(stream)
        }
        if (!isMain && TextUtils.isEmpty(split[2]) && streams.size > 0) {
            streams.get(0).setSubOutput(true)
        }
        if (isMain && TextUtils.isEmpty(split[3]) && streams.size > 0) {
            streams.get(0).setMainOutput(true)
        }
        return ListWrapper(streams)
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}