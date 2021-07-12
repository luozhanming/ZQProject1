package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.CaptionInfo
import cn.com.ava.lubosdk.entity.QueryResult
import java.util.*
/**
 * 查询录播画面字幕信息
 * */
class CaptionInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)?=null
) : IQuery<CaptionInfo> {


    override val name: String
        get() = "CaptionINfo"
    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.OSD_SUBTITLETEXT,
            AVATable.OSD_SUBTITLEDEFAULTTEXT1,
            AVATable.OSD_SUBTITLEDEFAULTTEXT2,
            AVATable.OSD_SUBTITLEDEFAULTTEXT3,
            AVATable.OSD_SUBTITLEDEFAULTTEXT4,
            AVATable.OSD_SUBTITLEDEFAULTTEXT5,
            AVATable.OSD_SUBTITLEVISIBLE,
            AVATable.OSD_SUBTITLEDISPTYPE

        )
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): CaptionInfo {
        val captions: MutableList<String> =
            ArrayList()
        if (!TextUtils.isEmpty(split[0])) {
            captions.add(split[0])
        }
        if (!TextUtils.isEmpty(split[1]) && !captions.contains(split[1])) {
            captions.add(split[1])
        }
        if (!TextUtils.isEmpty(split[2]) && !captions.contains(split[2])) {
            captions.add(split[2])
        }
        if (!TextUtils.isEmpty(split[3]) && !captions.contains(split[3])) {
            captions.add(split[3])
        }
        if (!TextUtils.isEmpty(split[4]) && !captions.contains(split[4])) {
            captions.add(split[4])
        }
        if (!TextUtils.isEmpty(split[5]) && !captions.contains(split[5])) {
            captions.add(split[5])
        }
        val isShowing = split.size > 6 && split[6] == "show"
        val isRolling = split.size > 7 && split[7] != "NULL"
        val info = CaptionInfo()
        info.captions = captions
        info.isShowing = isShowing
        info.isRolling = isRolling
        return info
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}