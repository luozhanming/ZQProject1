package cn.com.ava.zqproject.ui.videoResource.pop

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cn.com.ava.base.ui.BasePopupWindow
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.delay
import java.util.*
import java.util.logging.Handler
import kotlin.concurrent.schedule

class VideoResourceSortMenuWindow(context: Context, width: Int, height: Int) : PopupWindow() {
    // 按文件大小排序的回调
    var mSortBySizeCallback: () -> Unit = {}
    // 按录制时间的回调
    var mSortByTimeCallback: () -> Unit = {}


    lateinit var sizeTV : TextView
    lateinit var timeTV : TextView
    lateinit var view1 : ConstraintLayout
    lateinit var view2 : ConstraintLayout
    lateinit var selectedIc1 : ImageView
    lateinit var selectedIc2 : ImageView

    init {
        this.width = width
        this.height = height
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.ppw_video_sort_menu, null)
        this.contentView = view
        this.isTouchable = true
        this.isOutsideTouchable = true
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        this.setBackgroundDrawable(colorDrawable) // 设置背景


        sizeTV = view.findViewById(R.id.tv_sort_size)
        sizeTV.setOnClickListener {
            setIndex(0)
            mSortBySizeCallback.invoke()
            android.os.Handler().postDelayed({
                dismiss()
            }, 100)
        }

        timeTV = view.findViewById(R.id.tv_sort_time)
        timeTV.setOnClickListener {
            setIndex(1)
            mSortByTimeCallback.invoke()
            android.os.Handler().postDelayed({
                dismiss()
            }, 100)
        }

        view1 = view.findViewById(R.id.bg_sort_size)
        view2 = view.findViewById(R.id.bg_sort_time)
        selectedIc1 = view.findViewById(R.id.menu_selected)
        selectedIc2 = view.findViewById(R.id.menu_selected2)
    }

    fun sortBySize(callback: () -> Unit) {
        mSortBySizeCallback = callback
    }

    fun sortByTime(callback: () -> Unit) {
        mSortByTimeCallback = callback
    }

    override fun showAsDropDown(anchor: View?) {
        super.showAsDropDown(anchor, -SizeUtils.dp2px(160), SizeUtils.dp2px(10))
    }

    fun setIndex(index: Int) {
        val selectDrawable = ColorDrawable(Color.parseColor("#318EF8"))
        val normalDrawable = ColorDrawable(Color.WHITE)
        if (index == 0) {
            view1.background = selectDrawable
            view2.background = normalDrawable
            sizeTV.setTextColor(Color.WHITE)
            timeTV.setTextColor(Color.BLACK)
            selectedIc1.visibility = View.VISIBLE
            selectedIc2.visibility = View.GONE
//            Utils.getApp().resources.getColor(R.color.color_318EF8)
        } else {
            view2.background = selectDrawable
            view1.background = normalDrawable
            sizeTV.setTextColor(Color.BLACK)
            timeTV.setTextColor(Color.WHITE)
            selectedIc1.visibility = View.GONE
            selectedIc2.visibility = View.VISIBLE
        }
    }

}