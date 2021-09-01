package cn.com.ava.common.extension

import android.app.Activity
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.fragment.app.Fragment
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

/**
 * 避免spinner展开时popupwindow获取焦点从而使导航栏出现
 * */
fun Spinner.avoidDropdownFocus() {
    try {
        val isAppCompat = this is androidx.appcompat.widget.AppCompatSpinner
        val spinnerClass =
            if (isAppCompat) androidx.appcompat.widget.AppCompatSpinner::class.java else Spinner::class.java
        val popupWindowClass =
            if (isAppCompat) androidx.appcompat.widget.ListPopupWindow::class.java else android.widget.ListPopupWindow::class.java

        val listPopup = spinnerClass
            .getDeclaredField("mPopup")
            .apply { isAccessible = true }
            .get(this)
        if (popupWindowClass.isInstance(listPopup)) {
            val popup = popupWindowClass
                .getDeclaredField("mPopup")
                .apply { isAccessible = true }
                .get(listPopup)
            if (popup is PopupWindow) {
                popup.isFocusable = false
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun Char.isChinese(): Boolean {
    return compareTo('\u4e00') >= 0 && compareTo('\u9fa5') <= 0
}


