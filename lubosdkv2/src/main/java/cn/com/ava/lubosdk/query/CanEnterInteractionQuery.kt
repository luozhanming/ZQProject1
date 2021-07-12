package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.SimpleWrapper


/**
 * 录播能否进入互动界面
 * */
class CanEnterInteractionQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<SimpleWrapper<Boolean>> {


    override val name: String
        get() = "CanEnterInteraction"
    override val mQueryFields: Array<Int>
        get() = arrayOf(AVATable.SEM_INTERAMODE,AVATable.REC_INFO,AVATable.RTSP_INFO)
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): SimpleWrapper<Boolean> {
        val isRecordMode =
            split[0] == "record" || TextUtils.isEmpty(split[0])
        val isLiving = !TextUtils.isEmpty(split[1])&&split[2] != "-1"
        val isRecording = !TextUtils.isEmpty(split[1])&&split[1] != "0 -1"
        return SimpleWrapper(!(isRecordMode && (isLiving || isRecording)))
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}