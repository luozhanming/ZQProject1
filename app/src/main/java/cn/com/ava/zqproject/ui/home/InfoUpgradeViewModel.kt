package cn.com.ava.zqproject.ui.home

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.entity.LuBoInfo
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.zqproject.AppConfig
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.common.CommonPreference
import cn.com.ava.zqproject.common.MyDownloadManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.net.PlatformApiManager
import cn.com.ava.zqproject.vo.AppUpgrade
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.schedulers.Schedulers

class InfoUpgradeViewModel : BaseViewModel() {

    val luboInfo: MutableLiveData<LuBoInfo> by lazy {
        MutableLiveData()
    }

    val hasNewVersion: OneTimeLiveData<AppUpgrade?> by lazy {
        OneTimeLiveData()
    }

    private var platformAddr by CommonPreference(CommonPreference.KEY_PLATFORM_ADDR, "")

    /**
     * 加载数据信息
     * */
    fun loadMachineInfo() {
        mDisposables.add(
            GeneralManager.getLuboInfo()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    luboInfo.postValue(it)
                }, {
                    logPrint2File(it,"InfoUpgradeViewModel#loadMachineInfo")
                })
        )
    }

    /**
     * 请求新版本
     * */
    fun requestNewVersion() {
        mDisposables.add(
            PlatformApi.getService().requestUpgrade()
                .compose(PlatformApi.applySchedulers())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val versionCode = AppUtils.getAppVersionCode()
                    val remote = it.data
                    if (remote.version > versionCode) {  //需要升级,弹出 提示升级
                        hasNewVersion.postValue(OneTimeEvent(it.data))
                    }else{
                        hasNewVersion.postValue(OneTimeEvent(null))

                    }
                }, {
                    logPrint2File(it,"InfoUpgradeViewModel#requestNewVersion")
                })
        )
    }

}