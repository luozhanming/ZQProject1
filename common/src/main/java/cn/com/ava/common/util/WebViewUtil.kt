package cn.com.ava.common.util

import android.content.Context
import android.os.Build
import android.util.ArrayMap
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.Utils
import java.io.FileInputStream
import java.io.InputStream

object WebViewUtil {


    /**本地缓存的网页资源*/
    private val useCssJsMap:ArrayMap<String,String> by lazy {
        val map = ArrayMap<String,String>()
        map
    }


    fun getH5WebView(): WebView {
        val webView = WebView(Utils.getApp())
        webView.settings.apply {
            javaScriptEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
            loadWithOverviewMode = true
            displayZoomControls = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            javaScriptCanOpenWindowsAutomatically = false
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        webView.setInitialScale(50)
        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }


            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val url = request?.url?.path?:""
                val lastSlash  = url.lastIndexOf("/")
                if(lastSlash!=-1){
                    val lastPath = url.substring(lastSlash+1)
                    var mimeType:String = ""
                    if(lastPath in useCssJsMap){  //如果拦截的js在本地，读取本地
                        if(lastPath.endsWith(".css")){
                            mimeType = "text/css"
                        }else if(lastPath.endsWith(".js")){
                            mimeType = "application/x-javascript"
                        }else{
                            mimeType = "text/html"
                        }
                        //获取资源InputStream
                        val ins = Utils.getApp().assets.open("offline_res/${lastPath}")
                        return WebResourceResponse(mimeType,"UTF-8",ins)
                    }
                }
                return super.shouldInterceptRequest(view, request)
            }
        }
        return webView
    }

    fun destroy(webView: WebView){
        val parent = webView.parent
        if(parent!=null&&parent is ViewGroup){
            parent.removeView(webView)
        }
        webView.stopLoading()
        webView.removeAllViews()
        webView.destroy()
    }
}