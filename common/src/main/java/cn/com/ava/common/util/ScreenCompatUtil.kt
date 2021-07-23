package cn.com.ava.common.util

import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity

/**
 * 今日头条屏幕适配方案工具类
 * */
object ScreenCompatUtil {

  private var sNonCompatDensity: Float = 0f
  private var sNonCompatScaledDensity = 0f

  /**
   * 初始化屏幕适配
   * @param activity
   * @param app
   * @param isBaseWidth 是否根据屏幕宽度适配
   * @param baseLength 基准长度
   * */
  fun initScreenCompat(activity: AppCompatActivity, app: Application,
                       isBaseWidth:Boolean,baseLength:Int) {
    val appDisplayMetrics = app.resources.displayMetrics
    if (sNonCompatDensity == 0f) {
      sNonCompatDensity = appDisplayMetrics.density
      sNonCompatScaledDensity = appDisplayMetrics.scaledDensity
      app.registerComponentCallbacks(object : ComponentCallbacks {
        override fun onLowMemory() {
        }

        override fun onConfigurationChanged(newConfig: Configuration?) {
          if (newConfig != null && newConfig.fontScale > 0) {
            sNonCompatScaledDensity = app.resources.displayMetrics.scaledDensity
          }
        }
      })
    }
    //注意屏幕旋转，width和height将互换
    val targetDensity = (if(isBaseWidth)appDisplayMetrics.widthPixels else appDisplayMetrics.heightPixels).toFloat() / baseLength
    val targetScaledDensity = targetDensity * (sNonCompatScaledDensity / sNonCompatDensity)
    val targetDensityDpi = (160 * targetDensity).toInt()
    logd("targetDensity:${targetDensity}")

    appDisplayMetrics.densityDpi = targetDensityDpi
    appDisplayMetrics.density = targetDensity
    appDisplayMetrics.scaledDensity = targetScaledDensity

    val activityMetrics = activity.resources.displayMetrics
    activityMetrics.density = targetDensity
    activityMetrics.scaledDensity = targetScaledDensity
    activityMetrics.densityDpi = targetDensityDpi
  }

}