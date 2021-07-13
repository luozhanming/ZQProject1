package cn.com.ava.common.util

import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView

object WebViewUtil {

    fun getH5WebView(context: Context): WebView {
        val webView = WebView(context)
        webView.settings.apply {
            javaScriptEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = false
            setSupportZoom(false)
            displayZoomControls = false
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            javaScriptCanOpenWindowsAutomatically = false
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
        return webView
    }
}