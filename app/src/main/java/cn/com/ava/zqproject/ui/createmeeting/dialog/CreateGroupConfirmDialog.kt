package cn.com.ava.zqproject.ui.createmeeting.dialog

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import cn.com.ava.base.ui.BaseDialog
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R
import com.blankj.utilcode.util.ToastUtils

class CreateGroupConfirmDialog(val callback: ((String) -> Unit)?) : BaseDialog() {

    private var etGroupName: EditText? = null
    private var btnConfrim: TextView? = null
    private var btnCancel: TextView? = null

    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(
            SizeUtils.dp2px(590),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER,
            true
        )
    }

    override fun initView(root: View) {
        etGroupName = root.findViewById(R.id.et_meeting_theme)
        btnConfrim = root.findViewById(R.id.btn_sure)
        btnCancel = root.findViewById(R.id.btn_cancel)
        btnCancel?.setOnClickListener {
            dismiss()
        }
        btnConfrim?.setOnClickListener {
            if (TextUtils.isEmpty(etGroupName?.text.toString())) {
                ToastUtils.showShort(resources.getString(R.string.input_groupname))
            }
            callback?.invoke(etGroupName?.text.toString())
        }
        etGroupName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnConfrim?.isEnabled = !TextUtils.isEmpty(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }




    override fun onDestroy() {
        super.onDestroy()
        etGroupName = null
        btnConfrim = null
        btnCancel = null
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_create_group_confirm
    }
}