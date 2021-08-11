package cn.com.ava.zqproject.ui.home

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.entity.LuBoInfo
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.zqproject.net.PlatformApi
import com.blankj.utilcode.util.AppUtils
import io.reactivex.schedulers.Schedulers

class InfoUpgradeViewModel : BaseViewModel() {

    val luboInfo: MutableLiveData<LuBoInfo> by lazy {
        MutableLiveData()
    }

    val hasNewVersion: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

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
                    logPrint2File(it)
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
                        hasNewVersion.postValue(true)
                    }else{
                        hasNewVersion.postValue(false)
                    }
                }, {
                    logPrint2File(it)
                })
        )
    }
}