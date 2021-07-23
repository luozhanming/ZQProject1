package cn.com.ava.zqproject.ui.login

import android.webkit.JavascriptInterface
import androidx.lifecycle.ViewModel
import cn.com.ava.common.util.logd
import cn.com.ava.zqproject.common.CommonPreference

/**
 * 平台登录ViewModel
 * @author luozhanming
 */
class LoginViewModel : ViewModel() {


    private var token: String? by CommonPreference(CommonPreference.KEY_PLATFORM_TOKEN, "")


    @JavascriptInterface
    fun getLuboInfo(): String {
        logd("getLuboInfo()")
        return ""
    }

    @JavascriptInterface
    fun onLoginResult(result: String) {
        //登录结果，登录后刷新一下token
    }


}