package cn.com.ava.zqproject.ui.login

import android.webkit.JavascriptInterface
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.com.ava.common.util.GsonUtil
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.CommandButton
import cn.com.ava.zqproject.vo.PlatformLogin
import cn.com.ava.zqproject.vo.PlatformResponse
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.google.gson.reflect.TypeToken
import okhttp3.internal.Util

/**
 * 平台登录ViewModel
 * @author luozhanming
 */
class LoginViewModel : ViewModel() {


    private var token: String? by CommonPreference(CommonPreference.KEY_PLATFORM_TOKEN, "")

     val goHome:MutableLiveData<Int> by lazy {
        val data = MutableLiveData<Int>()
        data.value = 0
        data
    }


    @JavascriptInterface
    fun getLuboInfo(): String {
        logd("getLuboInfo()")
        val ip = CommonPreference.getElement(CommonPreference.KEY_LUBO_IP,"")
        val port = CommonPreference.getElement(CommonPreference.KEY_LUBO_PORT,"")
        val username = LoginManager.getLogin()?.rserverInfo?.usr?:""
        val psw = CommonPreference.getElement(CommonPreference.KEY_LUBO_PASSWORD,"")
        val luboInfo =  """{
            "lubo_ip":"${ip}",
            "lubo_port":"${port}",
            "lubo_username":"${username}",
            "lubo_psw":"${psw}"
            }"""
        logd(luboInfo)
        return luboInfo
    }

    @JavascriptInterface
    fun onLoginResult(result: String) {
        //登录结果，登录后刷新一下token
        logd("${result}")
        val type = object : TypeToken<PlatformResponse<PlatformLogin>>() {}.type
        val response = GsonUtil.fromJson<PlatformResponse<PlatformLogin>>(result, type)
        logd(response.toString())
        if(response.success){
            //登录成功跳到主页
            goHome.postValue(goHome.value?.plus(1))
            PlatformApi.login(response.data)
        }
    }


}