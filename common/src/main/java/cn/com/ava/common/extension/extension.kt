package cn.com.ava.common.extension

import android.app.Activity
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.fragment.app.Fragment
import cn.com.ava.common.util.Extra
import java.text.SimpleDateFormat
import java.util.*

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

fun Long.toDateString(format: String): String {
    val df = SimpleDateFormat(format)
    val format1 = df.format(Date(this))
    return format1
}


fun String.toTimeStamp(format: String):Long{
    val df = SimpleDateFormat(format)
    val format1 = df.parse(this)
    return format1.time
}


fun Char.isChinese(): Boolean {
    return compareTo('\u4e00') >= 0 && compareTo('\u9fa5') <= 0
}

/**
 * 判断两个列表是否元素相同
 * */
fun <T> List<T>.sameTo(other:List<T>?):Boolean{
    if(this.size!=other?.size)return false
    forEachIndexed { index, t ->
        val otherItem = other[index]
        if(otherItem!=this[index]){
            return false
        }
    }
    return true
}


