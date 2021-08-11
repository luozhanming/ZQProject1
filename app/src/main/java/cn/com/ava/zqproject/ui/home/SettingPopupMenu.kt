package cn.com.ava.zqproject.ui.home

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IntDef
import cn.com.ava.base.ui.BasePopupWindow
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R

class SettingPopupMenu(context:Context,val onItemClicked:((Int)->Unit)?=null):BasePopupWindow(context) {

    private var btnInfo:TextView? = null
    private var btnPower:TextView? = null
    private var btnLogout:TextView? = null


    companion object{
        const val ITEM_INFO = 1 shl 1
        const val ITEM_POWER = 1 shl 2
        const val ITEM_LOGOUT = 1 shl 3
    }

    @IntDef(ITEM_INFO,ITEM_POWER,ITEM_LOGOUT)
    annotation class Item




    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(200),ViewGroup.LayoutParams.WRAP_CONTENT,true)
    }

    override fun getLayoutId(): Int {
        return R.layout.popupwindow_setting
    }

    override fun initView(root: View) {
        super.initView(root)
        btnInfo = root.findViewById(R.id.btn_machine_info)
        btnPower = root.findViewById(R.id.btn_power)
        btnLogout = root.findViewById(R.id.btn_logout)


        val onClicked = object :View.OnClickListener{
            override fun onClick(v: View?) {
                when(v?.id){
                    R.id.btn_machine_info->onItemClicked?.invoke(ITEM_INFO)
                    R.id.btn_power->onItemClicked?.invoke(ITEM_POWER)
                    R.id.btn_logout->onItemClicked?.invoke(ITEM_LOGOUT)
                    else->{}
                }
                dismiss()
            }
        }
        btnInfo?.setOnClickListener(onClicked)
        btnPower?.setOnClickListener(onClicked)
        btnLogout?.setOnClickListener(onClicked)
    }


    override fun onDismiss() {
        btnInfo = null
        btnPower = null
        btnLogout = null
    }
}