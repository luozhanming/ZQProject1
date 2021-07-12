package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.EffectInfo
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.util.EffectButtonHelper
import java.util.*

/**
 * 特效信息
 * */
class EffectInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<EffectInfo> {


    override val name: String
        get() = "EffectInfo"


    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.BLEND_TYPE,
            AVATable.BLEND_TIME,
            AVATable.UI_EFFECTSELECT
        )

    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): EffectInfo {
        val info = EffectInfo()
        val curEffectPart: String = split.get(0)
        val effectTimePart: String = split.get(1)
        val effectListPart: String = split.get(2)
        val effect: EffectInfo.Effect = EffectInfo.Effect()
        effect.effectCmd = curEffectPart
        effect.drawableId = EffectButtonHelper.getInstance().getEffectDrawable(curEffectPart)
        info.setCurEffect(effect)
        if (!TextUtils.isEmpty(effectTimePart)) {
            info.setEffectTime(effectTimePart.toInt())
        }
        val effects: MutableList<EffectInfo.Effect> =
            ArrayList()
        val split1 = effectListPart.split(",".toRegex()).toTypedArray()
        for (s in split1) {
            val split2 = s.split("/".toRegex()).toTypedArray()
            val effect1: EffectInfo.Effect = EffectInfo.Effect()
            effect1.effectCmd = split2[1]
            effect1.drawableId = EffectButtonHelper.getInstance().getEffectDrawable(split2[1])
            effect1.isCurEffect = effect1.effectCmd.equals(curEffectPart)
            effects.add(effect1)
        }
        info.setEffectList(effects)
        return info
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}