package cn.com.ava.zqproject.ui.record

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.MachineInfo
import cn.com.ava.lubosdk.manager.GeneralManager
import cn.com.ava.lubosdk.manager.RecordManager
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.vo.PlatformLogin
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import io.reactivex.schedulers.Schedulers

class RecordInfoViewModel : BaseViewModel() {

    val isEditingTheme: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    val machineInfo: MutableLiveData<MachineInfo> by lazy {
        MutableLiveData()
    }

    val platformLogin: MutableLiveData<PlatformLogin> by lazy {
        MutableLiveData<PlatformLogin>().apply {
            value = PlatformApi.getPlatformLogin()
        }
    }

    val recordParams by lazy {
        MediatorLiveData<String>().apply {
            addSource(machineInfo) { info ->
                value =
                    "${info.recordParams.width}*${info.recordParams.height}@${info.recordParams.gop},${info.recordParams.bps}Kbps"
            }
        }
    }

    val themeEditText by lazy {
        MutableLiveData<String>()
    }

    val diskSpace by lazy {
        MediatorLiveData<String>().apply {
            addSource(machineInfo) { info ->
                value = String.format(Utils.getApp().getString(R.string.file_space),
                "${info.remainSpace.nowSize}/${info.remainSpace.totalSize}")
            }
        }
    }


    fun getMahineInfo() {
        logd("${mDisposables.isDisposed}")
        mDisposables.add(
            GeneralManager.getMachineInfo()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    machineInfo.postValue(it)
                }, {
                    logPrint2File(it)
                })
        )
    }

    fun changeTheme() {
        val theme = themeEditText.value ?: ""
        logd("${mDisposables.isDisposed}")
        mDisposables.add(
            RecordManager.setClassInfo(theme, platformLogin.value?.name ?: "")
                .subscribeOn(Schedulers.io())
                .subscribe({
                    isEditingTheme.postValue(false)
                }, {
                    ToastUtils.showShort(Utils.getApp().getString(R.string.modify_theme_failed))
                    logPrint2File(it)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        recordParams.removeSource(machineInfo)
    }
}