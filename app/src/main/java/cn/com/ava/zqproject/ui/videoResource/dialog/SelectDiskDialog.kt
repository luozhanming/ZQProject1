package cn.com.ava.zqproject.ui.videoResource.dialog

import android.view.Gravity
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.Keep
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import cn.com.ava.base.ui.BaseDialog
import cn.com.ava.common.util.SizeUtils
import cn.com.ava.common.util.WebViewUtil
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import org.json.JSONObject

class SelectDiskDialog(var paths: List<String>, val callback: ((String) -> Unit)?)  : BaseDialog(){

    private var btnCancel: TextView? = null
    private var tvWarning: TextView? = null

    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(
            SizeUtils.dp2px(480),
            SizeUtils.dp2px(240),
            Gravity.CENTER,
            true)
    }

    override fun initView(root: View) {

        btnCancel = root.findViewById(R.id.btn_cancel)
        tvWarning = root.findViewById(R.id.tv_warning)
        btnCancel?.setOnClickListener {
            dismiss()
        }
        if (paths.size == 0) {
            tvWarning?.isVisible = true
        } else {
            tvWarning?.isVisible = false
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_select_disk
    }


}