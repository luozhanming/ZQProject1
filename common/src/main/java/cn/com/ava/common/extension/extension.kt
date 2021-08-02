package cn.com.ava.common.extension

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import androidx.fragment.app.Fragment
import cn.com.ava.common.R
import cn.com.ava.common.util.Extra

inline fun Activity.bindExtras() {
    val clazz = this::class.java
    val array = clazz.declaredFields
    val args = intent.extras
    array.forEach { field ->
        field.isAccessible = true
        val annotations = field.annotations
        annotations.forEach { annotation ->
            if (annotation is Extra) {
                val key = annotation.value
                if (args?.containsKey(key) == true) {//赋值
                    field.set(this, args[key])
                }
            }
        }
    }
}


inline fun Fragment.bindExtras() {
    val clazz = this::class.java
    val array = clazz.declaredFields
    val args = arguments
    array.forEach { field ->
        field.isAccessible = true
        val annotations = field.annotations
        annotations.forEach { annotation ->
            if (annotation is Extra) {
                val key = annotation.value
                if (args?.containsKey(key) == true) {//赋值
                    field.set(this, args[key])
                }
            }
        }
    }
}


fun View.bounceClick() {
    val animate = AnimationUtils.loadAnimation(context, R.anim.scale_small).apply {
        duration = 100
        interpolator = BounceInterpolator()
    }
    this.animation = animate
    animate.start()
    animate.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            this@bounceClick.animation.cancel()
            this@bounceClick.animation = null
            animate.setAnimationListener(null)
        }

        override fun onAnimationRepeat(animation: Animation?) {

        }

    })

}
