package cn.com.ava.common.util

import com.blankj.utilcode.util.Utils
import kotlin.math.roundToInt

object SizeUtils {


    fun dp2px(dp: Int): Int {
        val displayMetrics = Utils.getApp().resources.displayMetrics
        return (displayMetrics.density * dp).roundToInt()
    }

    fun sp2px(sp:Int):Int{
        val displayMetrics = Utils.getApp().resources.displayMetrics
        return displayMetrics.scaledDensity.toInt() * sp
    }
}