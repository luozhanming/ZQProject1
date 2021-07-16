package cn.com.ava.zqproject.ui.setting

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.LuBoSDK
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.ui.splash.SplashViewModel
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.Utils
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class LuBoSettingViewModel:BaseViewModel() {


    val ip:MutableLiveData<String> by lazy {
        val livedata = MutableLiveData<String>()
        livedata.value = CommonPreference.getElement(CommonPreference.KEY_LUBO_IP,"")
        livedata
    }

    val port:MutableLiveData<String> by lazy {
        val livedata = MutableLiveData<String>()
        livedata.value = CommonPreference.getElement(CommonPreference.KEY_LUBO_PORT,"")
        livedata
    }

    val username:MutableLiveData<String> by lazy {
        val livedata = MutableLiveData<String>()
        livedata.value = CommonPreference.getElement(CommonPreference.KEY_LUBO_USERNAME,"")
        livedata
    }


    val password:MutableLiveData<String> by lazy {
        val livedata = MutableLiveData<String>()
        livedata.value = CommonPreference.getElement(CommonPreference.KEY_LUBO_PASSWORD,"")
        livedata
    }

    val isShowWakeUp: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val goPlatformSetting:MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val goPlatformLogin:MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val toastMsg:MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    fun login() {
        //从SP中获取上次成功登录的录播用户名密码
        if (RegexUtils.isIP(ip.value) && TextUtils.isDigitsOnly(port.value)) {
            LuBoSDK.init(ip.value!!,port.value!!,true)
        } else {//标记输入不正确
            toastMsg.postValue(Utils.getApp().getString(R.string.toast_input_correct_lubo_info))
        }
        val username: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_USERNAME, "")
        val password: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_PASSWORD, "")
        val platformAddr: String = CommonPreference.getElement(CommonPreference.KEY_PLATFORM_ADDR, "")
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            // 提示用户名密码错误
            toastMsg.postValue(Utils.getApp().getString(R.string.toast_input_account_psw))
        } else {  //尝试登录
            mDisposables.add(
                LoginManager.newLogin(username, password)
                    .timeout(5000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .subscribe({ login ->
                        if (login.isLoginSuccess) {
                            if (login.isSleep) {  // 休眠弹出唤醒窗口
                                logd("录播休眠中..")
                                isShowWakeUp.postValue(true)
                            } else {   // 成功登录，跳到平台窗口
                                logd("录播登录成功..")
                                saveLuboAccount()
                                if(TextUtils.isEmpty(platformAddr)){
                                    goPlatformSetting.postValue(true)
                                }else{  // 跳到主页
                                    goPlatformLogin.postValue(true)
                                }
                            }
                        } else {// 失败弹出提示并跳到录播设置页面
                            logd("录播登录失败..")
                            toastMsg.postValue(Utils.getApp().getString(R.string.toast_lubo_login_failed))
                        }
                    }, {
                        logd("录播登录失败..")
                        logPrint2File(it)
                        toastMsg.postValue(Utils.getApp().getString(R.string.toast_lubo_login_failed))
                    })
            )
        }


    }

    private fun saveLuboAccount() {
        CommonPreference.putElement(CommonPreference.KEY_LUBO_IP,ip.value)
        CommonPreference.putElement(CommonPreference.KEY_LUBO_PORT,port.value)
        CommonPreference.putElement(CommonPreference.KEY_LUBO_USERNAME,username.value)
        CommonPreference.putElement(CommonPreference.KEY_LUBO_PASSWORD,password.value)


    }
}