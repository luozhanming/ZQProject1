package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.LastInteracSetting
import cn.com.ava.lubosdk.entity.QueryResult
/**
 * 上一次互动设置
 * */
class LastInteracSettingQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) :IQuery<LastInteracSetting> {

    override val name: String
        get() = "LastInteracSetting"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.RSC_LASTCONFTYPE,AVATable.RSC_LASTVIDEOCODEC,AVATable.RSC_LASTSTREAMMODE,AVATable.RSC_LASTMAXSHOWINGNUM)
    override var offsets: Int = 0
        get() =field
        set(value) {field = value}

    override fun build(split: List<String>): LastInteracSetting {
        val setting = LastInteracSetting()
        setting.confType = split.get(0)
        setting.videoCodec = split.get(1)
        setting.isDual = "DUAL" == split.get(2)
        if (!TextUtils.isEmpty(split.get(3))) {
            setting.lastMaxShow = split.get(3).toInt()
        }
        return setting
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}