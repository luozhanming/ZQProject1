package cn.com.ava.zqproject.ui.createmeeting.dialog

import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import cn.com.ava.base.ui.BaseDialog
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.common.widget.CharLengthInputFilter
import cn.com.ava.common.widget.SpecialCharInputFilter
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.net.PlatformApi
/**
 * 创建会议弹出的对话框
 * */
class CreateMeetingDialog(val callback: ((String, String, Boolean) -> Unit)?) : BaseDialog() {

    private var etMeetingTheme: EditText? = null
    private var etMeetingNickname: EditText? = null
    private var switchWaitingRoom: Switch? = null
    private var btnCancel: TextView? = null
    private var btnCall: TextView? = null



    private var mTextWatcher: TextWatcher? = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val theme = etMeetingTheme?.text?.toString()
            val nickname = etMeetingNickname?.text?.toString()
            btnCall?.isEnabled = !TextUtils.isEmpty(theme) && !TextUtils.isEmpty(nickname)
        }

        override fun afterTextChanged(s: Editable?) {
        }

    }


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(
            SizeUtils.dp2px(590),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER,
            true
        )
    }

    override fun initView(root: View) {
        etMeetingTheme = root.findViewById(R.id.et_meeting_theme)
        etMeetingNickname = root.findViewById(R.id.et_meeting_nickname)
        switchWaitingRoom = root.findViewById(R.id.switch_waiting_room)
        btnCall = root.findViewById(R.id.btn_sure)
        btnCancel = root.findViewById(R.id.btn_cancel)
        etMeetingTheme?.addTextChangedListener(mTextWatcher)
        etMeetingNickname?.addTextChangedListener(mTextWatcher)
        btnCall?.isEnabled = false
        btnCall?.setOnClickListener {
            val theme = etMeetingTheme?.text?.toString() ?: ""
            val nickname = etMeetingNickname?.text?.toString() ?: ""
            val waiting = switchWaitingRoom?.isChecked ?: false
            callback?.invoke(theme, nickname, waiting)
            dismiss()
        }
        btnCancel?.setOnClickListener {
            dismiss()
        }

        etMeetingTheme?.filters = arrayOf(SpecialCharInputFilter(),InputFilter.LengthFilter(20))
        etMeetingNickname?.filters = arrayOf(SpecialCharInputFilter(),CharLengthInputFilter(36))
        //设置默认
        val platformLogin = PlatformApi.getPlatformLogin()
        platformLogin?.apply {
            etMeetingTheme?.setText( "${name}${getString(R.string.who_launch_meeting)}")
            etMeetingNickname?.setText("${name}")
//            if(TextUtils.isEmpty(professionTitleName)){
//                etMeetingNickname?.setText("${name}")
//            }else{
//                etMeetingNickname?.setText("${name}_${professionTitleName}")
//            }
        }
    }


    override fun onDestroy() {
        etMeetingNickname?.removeTextChangedListener(mTextWatcher)
        etMeetingTheme?.removeTextChangedListener(mTextWatcher)
        mTextWatcher = null
        etMeetingTheme = null
        etMeetingNickname = null
        switchWaitingRoom = null
        btnCall = null
        btnCancel = null
        super.onDestroy()

    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_create_meeting
    }
}