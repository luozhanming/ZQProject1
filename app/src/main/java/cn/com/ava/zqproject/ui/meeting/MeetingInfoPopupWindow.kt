package cn.com.ava.zqproject.ui.meeting

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.com.ava.base.ui.BasePopupWindow
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.lubosdk.entity.ListenerInfo
import cn.com.ava.lubosdk.zq.entity.MeetingInfoZQ
import cn.com.ava.zqproject.R

/**
 * 听课信息弹出窗口
 * */
class MeetingInfoPopupWindow(context: Context) : BasePopupWindow(context) {

    private var tvTheme: TextView? = null
    private var tvMeetingNum: TextView? = null
    private var tvMeetingPsw: TextView? = null
    private var tvBeginTime: TextView? = null
    private var tvMasterName: TextView? = null

    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(SizeUtils.dp2px(640), ViewGroup.LayoutParams.WRAP_CONTENT, true)
    }

    override fun getLayoutId(): Int {
        return R.layout.popupwindow_listener_info
    }

    override fun initView(root: View) {
        tvTheme = root.findViewById(R.id.tv_theme)
        tvMeetingNum = root.findViewById(R.id.tv_meeting_num)
        tvMeetingPsw = root.findViewById(R.id.tv_meeting_psw)
        tvBeginTime = root.findViewById(R.id.tv_meeting_begin_time)
        tvMasterName = root.findViewById(R.id.tv_meeting_master_name)
    }


    fun setListenerInfo(info: ListenerInfo?) {
        tvTheme?.text = info?.meetingTheme
        tvMeetingNum?.text =
            "${getResources().getString(R.string.meeting_num)}${info?.meetingNumber}"
        tvMeetingPsw?.text =
            "${getResources().getString(R.string.meeting_psw)}${info?.meetingPassword}"
    }

    fun setMeetingMasterInfo(info: MeetingInfoZQ?) {
        tvTheme?.text = info?.confTheme
        tvMeetingNum?.text = "${getResources().getString(R.string.meeting_num)}${info?.confId}"
        tvMeetingPsw?.text =
            "${getResources().getString(R.string.meeting_psw)}${info?.confpsw ?: ""}"
        tvBeginTime?.text =
            "${getResources().getString(R.string.meeting_begin_time)}${info?.confStartTime ?: ""}"
    }

    fun setMasterUser(value: List<LinkedUser>?) {
        value?.apply {
            if (value.isNotEmpty()) {
                val master = value.firstOrNull { it.number==1 }
                tvMasterName?.text =
                    "${getResources().getString(R.string.meeting_master_name)}${master?.nickname ?: ""}"
            }
        }

    }
}