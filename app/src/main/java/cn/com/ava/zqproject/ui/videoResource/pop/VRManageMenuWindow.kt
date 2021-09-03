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
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R

class VRManageMenuWindow(context: Context, width: Int, height: Int) : PopupWindow() {
    // 批量管理的回调
    var mBatchManageCallback: () -> Unit = {}

    lateinit var batchManageTV : TextView

    init {
        this.width = width
        this.height = height
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.ppw_video_manage_menu, null)
        this.contentView = view
        this.isTouchable = true
        this.isOutsideTouchable = true
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        this.setBackgroundDrawable(colorDrawable) // 设置背景


        batchManageTV = view.findViewById(R.id.tv_batch_manage)
        batchManageTV.setOnClickListener {
            mBatchManageCallback.invoke()
            dismiss()
        }
    }

    fun batchManage(callback: () -> Unit) {
        mBatchManageCallback = callback
    }

    override fun showAsDropDown(anchor: View?) {
        super.showAsDropDown(anchor, -SizeUtils.dp2px(140), SizeUtils.dp2px(10))
    }

}