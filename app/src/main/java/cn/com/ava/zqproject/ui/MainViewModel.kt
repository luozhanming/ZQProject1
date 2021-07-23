package cn.com.ava.zqproject.ui

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.LuBoSDK
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.spquery.RecordFilesQuery
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainViewModel : BaseViewModel() {

    val isShowLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }




}