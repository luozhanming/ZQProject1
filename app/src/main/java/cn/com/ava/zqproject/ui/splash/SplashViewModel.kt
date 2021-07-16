package cn.com.ava.zqproject.ui.splash

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.LuBoSDK
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.zqproject.common.CommonPreference
import com.blankj.utilcode.util.RegexUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 欢迎界面
 */
class SplashViewModel : BaseViewModel() {

    companion object {
        const val WHERE_NONE = 1001
        const val WHERE_LUBO_SETTING = 1002
        const val WHERE_PLATFORM_SETTING = 1003
        const val WHERE_PLATFORM_LOGIN = 1004
    }

    val isShowWakeUp: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val goWhere:MutableLiveData<Int> by lazy {
        val livedata = MutableLiveData<Int>()
        livedata.postValue(WHERE_NONE)
        livedata
    }


    fun login() {
        //从SP中获取上次成功登录的录播用户名密码
        val ip: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_IP, "")
        val port: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_PORT, "")
        if (RegexUtils.isIP(ip) && TextUtils.isDigitsOnly(port)) {
            LuBoSDK.init(ip,port,true)
        } else {// 跳到录播设置界面
            goWhere.postValue(WHERE_LUBO_SETTING)
        }
        val username: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_USERNAME, "")
        val password: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_PASSWORD, "")
        val platformAddr: String = CommonPreference.getElement(CommonPreference.KEY_PLATFORM_ADDR, "")
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            // 跳到录播设置界面
            goWhere.value = WHERE_LUBO_SETTING
        } else {  //尝试登录
            mDisposables.add(
                LoginManager.newLogin(username, password)
                    .timeout(5000,TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .subscribe({ login ->
                        if (login.isLoginSuccess) {
                            if (login.isSleep) {  // 休眠弹出唤醒窗口
                                logd("录播休眠中..")
                                isShowWakeUp.postValue(true)
                            } else {   // 成功登录，跳到平台窗口
                                logd("录播登录成功..")
                                if(TextUtils.isEmpty(platformAddr)){
                                    goWhere.postValue(WHERE_PLATFORM_SETTING)
                                }else{
                                    goWhere.postValue(WHERE_PLATFORM_LOGIN)
                                }
                            }
                        } else {// 失败弹出提示并跳到录播设置页面
                            logd("录播登录失败..")
                            goWhere.postValue(WHERE_LUBO_SETTING)
                        }
                    }, {
                        logd("录播登录失败..")
                        logPrint2File(it)
                        goWhere.postValue(WHERE_LUBO_SETTING)
                    })
            )
        }


    }

    fun startTimeout() {
        mDisposables.add(Observable.timer(2,TimeUnit.MILLISECONDS)
            .subscribe({
                goWhere.postValue(WHERE_LUBO_SETTING)
            },{

            }))
    }


}