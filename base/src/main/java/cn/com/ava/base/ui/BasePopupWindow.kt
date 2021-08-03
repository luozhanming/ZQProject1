package cn.com.ava.base.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.annotation.LayoutRes


abstract class BasePopupWindow(val context: Context) : PopupWindow(context) {

    init {
        val root = LayoutInflater.from(context).inflate(getLayoutId(), null)
        contentView = root
        val options:WindowOptions = getWindowOptions()
        width = options.width
        height = options.height
        isOutsideTouchable = options.canTouchOutsideCancel
        val dw = ColorDrawable(Color.TRANSPARENT)
        setBackgroundDrawable(dw)
        initView(root)

    }

    abstract fun getWindowOptions(): WindowOptions

    open fun initView(root:View) {

    }

    @LayoutRes
    abstract fun getLayoutId(): Int


    data class WindowOptions(
        val width: Int,
        val height: Int,
        val canTouchOutsideCancel: Boolean
    )
}