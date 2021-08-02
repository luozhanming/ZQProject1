package cn.com.ava.zqproject.ui.videoResource

import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel

class VideoTransmissionListViewModel : BaseViewModel() {

    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

}