package cn.com.ava.zqproject.ui.videoResource

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
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

    val filterVideos: MutableLiveData<List<RecordFilesInfo.RecordFile>> by lazy {
        MediatorLiveData<List<RecordFilesInfo.RecordFile>>().apply {
            //value = arrayListOf()
            addSource(searchKey) { key ->
                // 关键词过滤规则
                val list = videoResources.value
                val filter = list?.filter {
                    return@filter (it.downloadFileName.contains(key))
                }
                postValue(filter)
            }
        }
    }

}