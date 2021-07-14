package cn.com.ava.base.ui

import android.content.Context
import android.view.LayoutInflater
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
        initView()

    }

    abstract fun getWindowOptions(): WindowOptions

    fun initView() {

    }

    @LayoutRes
    abstract fun getLayoutId(): Int


    data class WindowOptions(
        val width: Int,
        val height: Int,
        val canTouchOutsideCancel: Boolean
    )
}