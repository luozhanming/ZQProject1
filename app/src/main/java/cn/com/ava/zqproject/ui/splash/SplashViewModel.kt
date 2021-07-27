package cn.com.ava.zqproject.ui.splash

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.LuBoSDK
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.manager.PowerManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.net.PlatformApiManager
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
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

    var stayRange = System.currentTimeMillis()..System.currentTimeMillis() + 2000
        get() = field

    //是否弹出唤醒
    val isShowWakeUp: MutableLiveData<Int> by lazy {
        val livedata = MutableLiveData<Int>()
        livedata.value = 0
        livedata

    }

    //跳去那里
    val goWhere: MutableLiveData<Int> by lazy {
        val livedata = MutableLiveData<Int>()
        livedata.postValue(WHERE_NONE)
        livedata
    }

    val isShowLoading: MutableLiveData<Boolean> by lazy {
        val livedata = MutableLiveData<Boolean>()
        livedata
    }


    val mPlatformToken: String by CommonPreference(CommonPreference.KEY_PLATFORM_TOKEN, "")


    fun login() {
        //从SP中获取上次成功登录的录播用户名密码
        val ip: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_IP, "")
        val port: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_PORT, "")
        if (RegexUtils.isIP(ip) && TextUtils.isDigitsOnly(port)) {
            LuBoSDK.init(ip, port, true)
        } else {// 跳到录播设置界面
            goWhere.postValue(WHERE_LUBO_SETTING)
            return
        }
        val username: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_USERNAME, "")
        val password: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_PASSWORD, "")
        val platformAddr: String =
            CommonPreference.getElement(CommonPreference.KEY_PLATFORM_ADDR, "")
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            // 跳到录播设置界面
            goWhere.value = WHERE_LUBO_SETTING
            return
        } else {  //尝试登录
            mDisposables.add(
                LoginManager.newLogin(username, password)
                    .subscribeOn(Schedulers.io())
                    .subscribe({ login ->
                        if (login.isLoginSuccess) {
                            if (login.isSleep) {  // 休眠弹出唤醒窗口
                                logd("录播休眠中..")
                                isShowWakeUp.postValue(isShowWakeUp.value?.plus(1))
                            } else {   // 成功登录，跳到平台窗口
                                logd("录播登录成功..")
                                if (TextUtils.isEmpty(platformAddr)) {
                                    goWhere.postValue(WHERE_PLATFORM_SETTING)
                                } else {
                                    loadCanEnterHome()
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


    /**
     * 根据保存的token看是否能够直接进入主界面
     */
    private fun loadCanEnterHome() {
        logd("loadCanEnterHome")
        val token = CommonPreference.getElement(CommonPreference.KEY_PLATFORM_TOKEN, "")
        val platformUrl = CommonPreference.getElement(CommonPreference.KEY_PLATFORM_ADDR, "")
        if (!TextUtils.isEmpty(token)) {
            //TODO 调用刷新接口按钮
        } else {
            //1.调用平台接口页面看是否能够连接平台
            //2.如果能够连接跳到平台登录，如果不能跳到平台设置页面
            mDisposables.add(
                PlatformApi.getService(platformUrl)
                    .getInterface().compose(PlatformApi.applySchedulers())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response ->
                        PlatformApiManager.setApiPath(response.data)
                        goWhere.postValue(WHERE_PLATFORM_LOGIN)
                    }, {
                        logd("平台连接失败..")
                        ToastUtils.showShort(
                            Utils.getApp().getString(R.string.toast_platform_link_failed)
                        )
                        logPrint2File(it)
                        goWhere.postValue(WHERE_PLATFORM_SETTING)
                    })
            )
        }
    }

    fun wakeupMachine() {
        isShowLoading.postValue(true)
        mDisposables.add(PowerManager.wakeupMachine()
            .flatMap {
                Observable.timer(70, TimeUnit.SECONDS)
            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                //唤醒完登录
                isShowLoading.postValue(false)
                login()
            }, {
                isShowLoading.postValue(false)
                logPrint2File(it)
                goWhere.postValue(WHERE_LUBO_SETTING)
            })
        )
    }


}