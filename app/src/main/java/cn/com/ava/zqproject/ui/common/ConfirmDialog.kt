package cn.com.ava.zqproject.ui.common

import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.com.ava.base.ui.BaseDialog
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.zqproject.R

/**
 * 确认对话框
 *
 */
class ConfirmDialog(
    val message: String, val cancelOutside: Boolean = true,
    val onSure: ((DialogInterface?) -> Unit)? = null,
    val onCancel: ((DialogInterface?) -> Unit)?=null
) : BaseDialog() {

    private var tvMessage by autoCleared<TextView>()
    private var btnCancel  by autoCleared<TextView>()
    private var btnSure  by autoCleared<TextView>()


    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(
            SizeUtils.dp2px(480),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER,
            cancelOutside
        )
    }

    override fun initView(root: View) {

        tvMessage = root.findViewById(R.id.tv_message)
        btnCancel = root.findViewById(R.id.btn_cancel)
        btnSure = root.findViewById(R.id.btn_sure)
        tvMessage?.text = message
        btnCancel?.setOnClickListener {
            onCancel?.invoke(dialog)
            dismiss()
        }
        btnSure?.setOnClickListener {
            onSure?.invoke(dialog)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_confirm
    }

}