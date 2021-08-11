package cn.com.ava.zqproject.ui.setting

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
import cn.com.ava.zqproject.extension.toServerException
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.net.PlatformApiManager
import cn.com.ava.zqproject.ui.splash.SplashViewModel
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class LuBoSettingViewModel : BaseViewModel() {

    val tabIndex: MutableLiveData<Int> by lazy {
        val livedata = MutableLiveData<Int>()
        livedata.value = 0
        livedata
    }

    val ip: MutableLiveData<String> by lazy {
        val livedata = MutableLiveData<String>()
        livedata.value = CommonPreference.getElement(CommonPreference.KEY_LUBO_IP, "")
        livedata
    }

    val port: MutableLiveData<String> by lazy {
        val livedata = MutableLiveData<String>()
        livedata.value = CommonPreference.getElement(CommonPreference.KEY_LUBO_PORT, "")
        livedata
    }

    val username: MutableLiveData<String> by lazy {
        val livedata = MutableLiveData<String>()
        livedata.value = CommonPreference.getElement(CommonPreference.KEY_LUBO_USERNAME, "")
        livedata
    }


    val password: MutableLiveData<String> by lazy {
        val livedata = MutableLiveData<String>()
        livedata.value = CommonPreference.getElement(CommonPreference.KEY_LUBO_PASSWORD, "")
        livedata
    }


    val platformAddr: MutableLiveData<String> by lazy {
        val livedata = MutableLiveData<String>()
        livedata.value = CommonPreference.getElement(CommonPreference.KEY_PLATFORM_ADDR, "")
        livedata
    }

    //展示唤醒对话框
    val isShowWakeUp: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    //跳到平台设置
    val goPlatformSetting: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    //跳到平台登录
    val goPlatformLogin: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    //展示toast
    val toastMsg: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    //展示加载框
    val showLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val canBackShow: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }




    fun loadInitData() {
        val ip = CommonPreference.getElement(CommonPreference.KEY_LUBO_IP, "")
        val port = CommonPreference.getElement(CommonPreference.KEY_LUBO_PORT, "")
        val username: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_USERNAME, "")
        val password: String = CommonPreference.getElement(CommonPreference.KEY_LUBO_PASSWORD, "")
        val platformAddr: String =
            CommonPreference.getElement(CommonPreference.KEY_PLATFORM_ADDR, "")
        this.ip.value = ip
        this.port.value = port
        this.username.value = username
        this.password.value = password
        this.platformAddr.value = platformAddr
    }

    /**
     * 登录
     */
    fun login() {
        logd("${ip.value}/${port.value}/${username.value}/${password.value}")
        //从SP中获取上次成功登录的录播用户名密码
        if (RegexUtils.isIP(ip.value) && TextUtils.isDigitsOnly(port.value)) {
            LuBoSDK.init(ip.value!!, port.value!!, true)
        } else {//标记输入不正确
            toastMsg.postValue(Utils.getApp().getString(R.string.toast_input_correct_lubo_info))
        }
        val platformAddr: String =
            CommonPreference.getElement(CommonPreference.KEY_PLATFORM_ADDR, "")
        if (TextUtils.isEmpty(username.value) || TextUtils.isEmpty(password.value)) {
            // 提示用户名密码错误
            toastMsg.postValue(Utils.getApp().getString(R.string.toast_input_account_psw))
        } else {  //尝试登录
            showLoading.postValue(true)
            mDisposables.add(
                LoginManager.newLogin(username.value ?: "", password.value ?: "")
                    .subscribeOn(Schedulers.io())
                    .subscribe({ login ->
                        showLoading.postValue(false)
                        if (login.isLoginSuccess) {
                            if (login.isSleep) {  // 休眠弹出唤醒窗口
                                logd("录播休眠中..")
                                isShowWakeUp.postValue(1)
                            } else {   // 成功登录，跳到平台窗口
                                logd("录播登录成功..")
                                checkCanBackShow()
                                toastMsg.postValue(
                                    Utils.getApp().getString(R.string.toast_lubo_login_success)
                                )
                                saveLuboAccount()
                                if (TextUtils.isEmpty(platformAddr)) {
                                    goPlatformSetting.postValue(true)
                                } else {  // 跳到主页
                                    goPlatformLogin.postValue(true)
                                }
                            }
                        } else {// 失败弹出提示并跳到录播设置页面
                            logd("录播登录失败..")
                            toastMsg.postValue(
                                Utils.getApp().getString(R.string.toast_lubo_login_failed)
                            )
                        }
                    }, {
                        showLoading.postValue(false)
                        logd("录播登录失败..")
                        logPrint2File(it)
                        toastMsg.postValue(
                            Utils.getApp().getString(R.string.toast_lubo_login_failed)
                        )
                    })
            )
        }
    }


    /**
     * 获取平台接口,也作为连接用
     */
    fun loadPlatformInterface() {
        var addr = platformAddr.value
        if (TextUtils.isEmpty(addr)) {
            toastMsg.postValue(Utils.getApp().getString(R.string.input_platform_addr))
            return
        }
        addr = if (addr?.startsWith("https://") == true || addr?.startsWith("http://") == true) {
            addr
        } else {
            "http://${addr}"
        }

        showLoading.postValue(true)
        mDisposables.add(
            PlatformApi.getService(addr).getInterface()
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    showLoading.postValue(false)
                    checkCanBackShow()
                    toastMsg.postValue(
                        Utils.getApp().getString(R.string.toast_platform_link_success)
                    )
                    CommonPreference.putElement(
                        CommonPreference.KEY_PLATFORM_ADDR,
                        addr
                    )
                    PlatformApiManager.setApiPath(it.data)
                }, {
                    showLoading.postValue(false)
                    logPrint2File(it)
                    it.toServerException()?.apply {
                        toastMsg.postValue(message)
                    } ?: toastMsg.postValue(
                        Utils.getApp().getString(R.string.toast_platform_link_failed)
                    )
                })
        )
    }


    private fun checkCanBackShow() {
        if (LoginManager.isLogin() && PlatformApiManager.getApiPath(PlatformApiManager.PATH_WEBVIEW_LOGIN) != null) {
            canBackShow.postValue(true)
        } else {
            canBackShow.postValue(false)
        }
    }

    private fun saveLuboAccount() {
        CommonPreference.putElement(CommonPreference.KEY_LUBO_IP, ip.value)
        CommonPreference.putElement(CommonPreference.KEY_LUBO_PORT, port.value)
        CommonPreference.putElement(CommonPreference.KEY_LUBO_USERNAME, username.value)
        CommonPreference.putElement(CommonPreference.KEY_LUBO_PASSWORD, password.value)
    }


    fun wakeupMachine() {
        showLoading.postValue(true)
        mDisposables.add(
            PowerManager.wakeupMachine()
            .flatMap {
                Observable.timer(75, TimeUnit.SECONDS)
            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                //唤醒完登录
                showLoading.postValue(false)
                login()
            }, {
                showLoading.postValue(false)
                logPrint2File(it)
                ToastUtils.showShort(Utils.getApp().getString(R.string.wakeup_failed))
            })
        )
    }
}