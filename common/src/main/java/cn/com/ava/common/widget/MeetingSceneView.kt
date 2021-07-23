package cn.com.ava.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.IntDef
import cn.com.ava.common.R

class MeetingSceneView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    ViewGroup(context, attrs, defStyle) {

    companion object {
        const val LAYOUT_1 = 1
        const val LAYOUT_2 = 2
        const val LAYOUT_3 = 3
        const val LAYOUT_4 = 4
        const val LAYOUT_6 = 6
        const val LAYOUT_8 = 8

    }

    @IntDef(LAYOUT_1, LAYOUT_2, LAYOUT_3, LAYOUT_4, LAYOUT_6, LAYOUT_8)
    annotation class Layout{}


    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    private var borderWidth: Int = 1


    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MeetingSceneView)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }

}