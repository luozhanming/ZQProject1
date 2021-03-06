package cn.com.ava.zqproject.ui.setting

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
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
import cn.com.ava.zqproject.ui.meeting.VolumeSceneDialog
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

    //?????????????????????
    val isShowWakeUp: OneTimeLiveData<Int> by lazy {
        OneTimeLiveData()
    }

    //??????????????????
    val goPlatformSetting: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    //??????????????????
    val goPlatformLogin: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    //??????toast
    val toastMsg: OneTimeLiveData<String> by lazy {
        OneTimeLiveData()
    }

    //???????????????
    val showLoading: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    val canBackShow: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val isShowLuboPsw: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
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
     * ??????
     */
    fun login() {
        logd("${ip.value}/${port.value}/${username.value}/${password.value}")
        //???SP???????????????????????????????????????????????????
        if (RegexUtils.isIP(ip.value) && TextUtils.isDigitsOnly(port.value)) {
            LuBoSDK.init(ip.value!!, port.value!!, true)
        } else {//?????????????????????
            toastMsg.postValue(
                OneTimeEvent(
                    Utils.getApp().getString(R.string.toast_input_correct_lubo_info)
                )
            )
            return
        }
        val platformAddr: String =
            CommonPreference.getElement(CommonPreference.KEY_PLATFORM_ADDR, "")
        if (TextUtils.isEmpty(username.value) || TextUtils.isEmpty(password.value)) {
            // ???????????????????????????
            toastMsg.postValue(
                OneTimeEvent(
                    Utils.getApp().getString(R.string.toast_input_account_psw)
                )
            )
        } else {  //????????????
            showLoading.postValue(OneTimeEvent(true))
            mDisposables.add(
                LoginManager.newLogin(username.value ?: "", password.value ?: "")
                    .subscribeOn(Schedulers.io())
                    .subscribe({ login ->
                        showLoading.postValue(OneTimeEvent(false))
                        if (login.isLoginSuccess) {
                            if (login.isSleep) {  // ????????????????????????
                                logd("???????????????..")
                                isShowWakeUp.postValue(OneTimeEvent(1))
                            } else {   // ?????????????????????????????????
                                logd("??????????????????..")
                                checkCanBackShow()
                                toastMsg.postValue(
                                    OneTimeEvent(
                                        Utils.getApp().getString(R.string.toast_lubo_login_success)
                                    )
                                )
                                saveLuboAccount()
//                                if (TextUtils.isEmpty(platformAddr)) {
//                                    goPlatformSetting.postValue(true)
//                                } else {  // ????????????
//                                    goPlatformLogin.postValue(true)
//                                }
                            }
                        } else {// ?????????????????????????????????????????????
                            logd("??????????????????..")
                            toastMsg.postValue(
                                OneTimeEvent(
                                    Utils.getApp().getString(R.string.toast_lubo_login_failed)
                                )
                            )
                        }
                    }, {
                        showLoading.postValue(OneTimeEvent(false))
                        logd("??????????????????..")
                        logPrint2File(it, "LuboSettingViewModel#login")
                        toastMsg.postValue(
                            OneTimeEvent(
                                Utils.getApp().getString(R.string.toast_lubo_login_failed)
                            )
                        )
                    })
            )
        }
    }


    /**
     * ??????????????????,??????????????????
     */
    fun loadPlatformInterface() {
        var addr = platformAddr.value
        if (TextUtils.isEmpty(addr)) {
            toastMsg.postValue(OneTimeEvent(Utils.getApp().getString(R.string.input_platform_addr)))
            return
        }
        addr = if (addr?.startsWith("https://") == true || addr?.startsWith("http://") == true) {
            addr
        } else {
            "http://${addr}"
        }

        showLoading.postValue(OneTimeEvent(true))
        mDisposables.add(
            PlatformApi.getService(addr).getInterface()
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    PlatformApiManager.setApiPath(it.data)
                    checkCanBackShow()
                    showLoading.postValue(OneTimeEvent(false))
                    toastMsg.postValue(
                        OneTimeEvent(Utils.getApp().getString(R.string.toast_platform_link_success))
                    )
                    CommonPreference.putElement(
                        CommonPreference.KEY_PLATFORM_ADDR,
                        addr
                    )

                }, {
                    showLoading.postValue(OneTimeEvent(false))
                    logPrint2File(it, "LuboSettingViewModel#loadPlatformInterface")
                    it.toServerException()?.apply {
                        toastMsg.postValue(OneTimeEvent(message ?: ""))
                    } ?: toastMsg.postValue(
                        OneTimeEvent(
                            Utils.getApp().getString(R.string.toast_platform_link_failed)
                        )
                    )
                })
        )
    }

    /**
     * ?????????????????????????????????
     * */
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
        showLoading.postValue(OneTimeEvent(true))
        mDisposables.add(
            PowerManager.wakeupMachine()
                .flatMap {
                    Observable.timer(75, TimeUnit.SECONDS)
                }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    //???????????????
                    showLoading.postValue(OneTimeEvent(false))
                    login()
                }, {
                    showLoading.postValue(OneTimeEvent(false))
                    logPrint2File(it)
                    ToastUtils.showShort(Utils.getApp().getString(R.string.wakeup_failed))
                })
        )
    }
}