package cn.com.ava.zqproject.ui.common.power

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.lubosdk.manager.PowerManager
import cn.com.ava.zqproject.R
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.schedulers.Schedulers

class PowerViewModel:BaseViewModel() {


    val turnoffMachine:MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val sleepMachine:MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val reloadMachine:MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }




    fun reloadMachine() {
        PowerManager.reloadMachine()
            .subscribeOn(Schedulers.io())
            .subscribe({
                reloadMachine.postValue(true)
                ToastUtils.showShort("${getResources().getString(R.string.reload_machine)}${getResources().getString(R.string.success)}")
            },{
                ToastUtils.showShort("${getResources().getString(R.string.reload_machine)}${getResources().getString(R.string.failed)}")
                logPrint2File(it)
            })
    }

    fun sleepMachine() {
        PowerManager.sleepMachine()
            .subscribeOn(Schedulers.io())
            .subscribe({
                sleepMachine.postValue(true)
                ToastUtils.showShort("${getResources().getString(R.string.sleep_machine)}${getResources().getString(R.string.success)}")
            },{
                ToastUtils.showShort("${getResources().getString(R.string.sleep_machine)}${getResources().getString(R.string.failed)}")
                logPrint2File(it)
            })
    }

    fun turnoffMachine() {
        PowerManager.turnOffMachine()
            .subscribeOn(Schedulers.io())
            .subscribe({
                turnoffMachine.postValue(true)
                ToastUtils.showShort("${getResources().getString(R.string.turnoff_machine)}${getResources().getString(R.string.success)}")
            },{
                ToastUtils.showShort("${getResources().getString(R.string.turnoff_machine)}${getResources().getString(R.string.failed)}")
                logPrint2File(it)
            })
    }
}