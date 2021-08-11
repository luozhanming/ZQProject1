package cn.com.ava.zqproject.ui.videoResource.dialog

import android.view.Gravity
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.Keep
import androidx.cardview.widget.CardView
import cn.com.ava.base.ui.BaseDialog
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.common.util.WebViewUtil
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import org.json.JSONObject

class DeleteVideoDialog(val message: String, val callback: (() -> Unit)?)  : BaseDialog() {

    private var tvMessage: TextView? = null
    private var btnCancel: TextView? = null
    private var btnSure: TextView? = null

    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(
            SizeUtils.dp2px(480),
            SizeUtils.dp2px(224),
            Gravity.CENTER,
            true)
    }

    override fun initView(root: View) {
        tvMessage = root.findViewById(R.id.tv_message)
        tvMessage?.setText(message)

        btnCancel = root.findViewById(R.id.btn_cancel)
        btnSure = root.findViewById(R.id.btn_sure)

        btnCancel?.setOnClickListener {
            dismiss()
        }
        btnSure?.setOnClickListener {
            callback?.invoke()
            dismiss()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_confirm
    }

}