package cn.com.ava.zqproject.ui.videoResource

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.lubosdk.entity.RecordFilesInfo

class SearchVideoViewModel : BaseViewModel() {

    val videoResources: MutableLiveData<List<RecordFilesInfo.RecordFile>> by lazy {
        val data = arrayListOf<cn.com.ava.lubosdk.entity.RecordFilesInfo.RecordFile>()
        val liveData = MutableLiveData<List<RecordFilesInfo.RecordFile>>()
        liveData.value = data
        liveData
    }

    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

}