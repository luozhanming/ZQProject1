package cn.com.ava.zqproject.ui.videoResource.dialog

import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.*
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
    private var btnDownload: TextView? = null
    private var spinner: Spinner? = null
    private var downloadPath = ""

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
        btnDownload = root.findViewById(R.id.btn_sure)
        spinner = root.findViewById(R.id.spinner)

        if (paths.size == 0) {
            tvWarning?.isVisible = true
        } else {
            tvWarning?.isVisible = false
        }

        btnCancel?.setOnClickListener {
            dismiss()
        }
        btnDownload?.setOnClickListener {
            if (TextUtils.isEmpty(downloadPath)) {
                return@setOnClickListener
            }
            dismiss()
            callback?.invoke(downloadPath)
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paths)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                downloadPath = paths[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_select_disk
    }


}