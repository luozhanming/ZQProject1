package cn.com.ava.zqproject.ui.common.recordupload

import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.mvvm.OneTimeEvent
import cn.com.ava.common.mvvm.OneTimeLiveData
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.manager.VideoResourceManager

class RecordUploadViewModel:BaseViewModel() {

    val isLoading:OneTimeLiveData<Boolean> by lazy {
        OneTimeLiveData()
    }

    val confirmUploadFile:OneTimeLiveData<RecordFilesInfo.RecordFile> by lazy {
        OneTimeLiveData()
    }



    fun uploadLatestRecord(){
        isLoading.postValue(OneTimeEvent(true))

    }
}