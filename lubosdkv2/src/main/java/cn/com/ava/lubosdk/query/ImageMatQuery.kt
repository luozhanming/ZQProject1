package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.ImageMatInfo
import cn.com.ava.lubosdk.entity.QueryResult
/**
 * 录播抠像设置信息
 * */
class ImageMatQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<ImageMatInfo> {

    override val name: String
        get() = "ImageMatInfo"

    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.GUI_KEYINGTHRESHOLD1,
            AVATable.GUI_KEYINGTHRESHOLD2,
            AVATable.GUI_KEYINGBG1,
            AVATable.GUI_KEYINGBG2,
            AVATable.GUI_KEYINGTHRESHOLD3,
            AVATable.GUI_KEYINGTHRESHOLD4,
            AVATable.UI_KEYINGTHRESHOLD4ENABLE,
            AVATable.UI_KEYINGHDMI3VOENABLE,
            AVATable.GUI_KEYINGHDMI3VO,
            AVATable.GUI_KEYINGHDMI3VOCOLOR,
            AVATable.GUI_KEYINGHDMI3VOML,
            AVATable.GUI_KEYINGHDMI3VOSL
        )

    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(split: List<String>): ImageMatInfo {
        val info = ImageMatInfo()
        val s1: String = split.get(0)
        if (!TextUtils.isEmpty(s1)) {
            info.keyingThreshold1 = s1.toInt()
        }
        val s2: String = split.get(1)
        if (!TextUtils.isEmpty(s2)) {
            info.keyingThreshold2 = s2.toInt()
        }
        val s3: String = split.get(2)
        if (!TextUtils.isEmpty(s3)) {
            info.keyingBG1 = s3
        }
        val s4: String = split.get(3)
        if (!TextUtils.isEmpty(s4)) {
            info.keyingBG2 = s4
        }
        val s5: String = split.get(4)
        if (!TextUtils.isEmpty(s5)) {
            info.keyingThreshold3 = s5.toInt()
        }
        val s6: String = split.get(5)
        if (!TextUtils.isEmpty(s6)) {
            info.keyingThreshold4 = s6.toInt()
        }
        val s7: String = split.get(6)
        if (!TextUtils.isEmpty(s7)) {
            info.isKeyingThreshold4Enable = s7 == "enable"
        }
        val s8: String = split.get(7)
        if (!TextUtils.isEmpty(s8)) {
            info.isKeyingHDMI3VoEnable = s8 == "enable"
        }
        val s9: String = split.get(8)
        if (!TextUtils.isEmpty(s9)) {
            info.keyingHDMI3Vo = s9.toInt()
        }
        val s10: String = split.get(9)
        if (!TextUtils.isEmpty(s10)) {
            info.keyingHDMI3VoColor = s10.toInt()
        }
        val s11: String = split.get(10)
        if (!TextUtils.isEmpty(s11)) {
            info.keyingHDMI3VoML = s11.toInt()
        }
        val s12: String = split.get(11)
        if (!TextUtils.isEmpty(s12)) {
            info.keyingHDMI3VoSL = s12.toInt()
        }
        return info

    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}