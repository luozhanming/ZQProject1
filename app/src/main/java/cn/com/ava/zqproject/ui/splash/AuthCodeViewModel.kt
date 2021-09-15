package cn.com.ava.zqproject.ui.splash

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cn.com.ava.authcode.AuthKeyUtil
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.zqproject.R
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ToastUtils

class AuthCodeViewModel : BaseViewModel() {

    val autoCode: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val validateResult: OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    val deviceCode: MutableLiveData<String> by lazy {
        MutableLiveData<String>().apply {
            val deviceId = DeviceUtils.getUniqueDeviceId()
            val first: String = deviceId.substring(0, 4)
            val last: String = deviceId.substring(deviceId.length - 3)
            value="${first}${last}"
        }
    }


    /**
     * 从磁盘获取激活码
     * */
    fun getCodeFromDisk() {
        val cdKey = AuthKeyUtil.getCDKey()
        if (TextUtils.isEmpty(cdKey)) {  //无法获取激活码
            ToastUtils.showShort(getResources().getString(R.string.get_no_code))
            return
        } else {
            autoCode.value = cdKey
        }
    }


    fun validateAuthCode() {
        val code = autoCode.value ?: ""
        val result = AuthKeyUtil.validateCode(code)
        validateResult.postValue(OneTimeEvent(result))
    }
}