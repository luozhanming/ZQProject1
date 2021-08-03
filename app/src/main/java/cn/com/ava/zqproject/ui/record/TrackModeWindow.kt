package cn.com.ava.zqproject.ui.record

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.com.ava.base.ui.BasePopupWindow
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.lubosdk.Constant
import cn.com.ava.zqproject.R

/**
 * @param context
 * @param trackMode 自动模式
 * @param callback 回调
 * @see Constant.CamTrackMode
 */
class TrackModeWindow(context:Context,val callback:((String)->Unit)?=null):BasePopupWindow(context) {


    private var trackMode:String = Constant.TRACK_HAND

    private var btnAuto:TextView? = null

    private var btnMid:TextView? = null

    private var btnHand:TextView? = null

    override fun getWindowOptions(): WindowOptions {

        return WindowOptions(SizeUtils.dp2px(192),SizeUtils.dp2px(220),true)
    }

    override fun getLayoutId(): Int {
        return R.layout.popupwindow_track_mode
    }

    override fun initView(root:View) {
        btnAuto = root.findViewById(R.id.btn_auto)
        btnMid = root.findViewById(R.id.btn_mid)
        btnHand = root.findViewById(R.id.btn_hand)
        btnAuto?.setOnClickListener {
            callback?.invoke(Constant.TRACK_FULL_AUTO)
        }
        btnMid?.setOnClickListener {
            callback?.invoke(Constant.TRACK_MID_AUTO)

        }
        btnHand?.setOnClickListener {
            callback?.invoke(Constant.TRACK_HAND)
        }
    }

    fun setTrackMode(@Constant.CamTrackMode mode:String){
        trackMode = mode
        btnAuto?.isSelected = trackMode==Constant.TRACK_FULL_AUTO
        btnMid?.isSelected = trackMode==Constant.TRACK_MID_AUTO
        btnHand?.isSelected = trackMode==Constant.TRACK_HAND
    }




}