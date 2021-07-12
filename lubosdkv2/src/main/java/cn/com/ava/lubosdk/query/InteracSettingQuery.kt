package cn.com.ava.lubosdk.query

import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.InteracSetting
import cn.com.ava.lubosdk.entity.QueryResult

/**
 *   互动设置信息
 * */
class InteracSettingQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<InteracSetting> {

    override val name: String
        get() = "InteracSetting"

    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.GUI_NETWORKPARAM, AVATable.SEM_INTERAPROTOCOL,
            AVATable.SEM_CONFVIDEOCODEC, AVATable.RSC_ASSWITCH,
            AVATable.SEM_INTERNALCLOUD
        )

    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): InteracSetting {
        val setting = InteracSetting()
        setting.netparam = split.get(0)
        setting.protocal = split.get(1)
        setting.codemode = split.get(2)
        setting.asswitch = split.get(3)
        setting.enableCloud = "1" == split.get(4)
        return setting
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}