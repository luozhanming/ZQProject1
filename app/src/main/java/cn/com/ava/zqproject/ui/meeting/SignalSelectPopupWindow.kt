package cn.com.ava.zqproject.ui.meeting

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.com.ava.base.ui.BasePopupWindow
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.ui.meeting.adapter.SignalSelectAdapter
import kotlin.math.sign

/**
 * 布局信号选择弹出框
 * */
class SignalSelectPopupWindow(context: Context,val callback:((Int,LinkedUser)->Unit)?=null):BasePopupWindow(context) {


    private var rvSignalSelect:RecyclerView?=null

    private var mSignalSelectAdapter:SignalSelectAdapter?=null

    /*信号索引*/
    private var signalIndex:Int = -1

    //信号
    private val signals:MutableList<LinkedUser> by lazy {
        mutableListOf()
    }

    fun setDatas(data:List<LinkedUser>){
        signals.clear()
        signals.addAll(data)
        mSignalSelectAdapter?.setDatas(data)
    }

    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(427),ViewGroup.LayoutParams.WRAP_CONTENT,true)
    }

    override fun getLayoutId(): Int {
        return R.layout.popupwindow_signal_select
    }

    override fun initView(root: View) {
        rvSignalSelect = root.findViewById(R.id.rv_signal_select)
        rvSignalSelect?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        mSignalSelectAdapter = SignalSelectAdapter{
            //按下选择信号
            callback?.invoke(signalIndex,it)
        }
        rvSignalSelect?.adapter = mSignalSelectAdapter
    }

    fun showAsDropDown(signalIndex:Int,anchor: View, xoff: Int, yoff: Int) {
        this.signalIndex = signalIndex
        super.showAsDropDown(anchor, xoff, yoff, Gravity.BOTTOM or Gravity.CENTER)
    }
}