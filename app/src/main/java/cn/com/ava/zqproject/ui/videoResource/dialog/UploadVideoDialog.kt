package cn.com.ava.zqproject.ui.videoResource.dialog

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import cn.com.ava.base.ui.BaseDialog
import cn.com.ava.common.util.GsonUtil
import cn.com.ava.common.util.WebViewUtil
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.net.PlatformApiManager
import cn.com.ava.zqproject.ui.login.LoginViewModel
import cn.com.ava.zqproject.ui.videoResource.VideoManageViewModel
import com.blankj.utilcode.util.SizeUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

class UploadVideoDialog(val callback: ((String) -> Unit)?)  : BaseDialog(){

    private var webViewContainer: CardView? = null

    private var mLastHtmlUrl: String? = null

    private val mWebView by lazy {
        val webView = WebViewUtil.getH5WebView()
        webView.addJavascriptInterface(JSObject(), "androidObj")
        webView
    }

    override fun getWindowOptions(): WindowOptions {
        return WindowOptions(
            cn.com.ava.common.util.SizeUtils.dp2px(618),
            cn.com.ava.common.util.SizeUtils.dp2px(446),
            Gravity.CENTER,
            true)
    }

    override fun initView(root: View) {
        webViewContainer = root.findViewById(R.id.webViewContainer)
        if (mWebView.parent == null) {
            webViewContainer?.addView(
                mWebView,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

//        val html = "${CommonPreference.getElement(CommonPreference.KEY_PLATFORM_ADDR, "")}" +
//                "/#/uploadInfo"
        val html = "http://192.168.35.222/#/uploadInfo"
        logd("Webview load ${html}")
        if (html != mLastHtmlUrl) {
            mWebView.loadUrl(
                html
            )
            mLastHtmlUrl = html
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_upload_video
    }

    @Keep
    inner class JSObject {
        @Keep
        @JavascriptInterface
        fun closeDialog() {
            dismiss()
        }

        @JavascriptInterface
        fun submitVideoInfo(info: String) {
            logd("info：$info")
            callback?.invoke(JSONObject(info).get("remark").toString())
            dismiss()
        }

    }

}