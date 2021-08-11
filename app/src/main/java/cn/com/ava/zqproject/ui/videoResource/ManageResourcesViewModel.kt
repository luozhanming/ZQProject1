package cn.com.ava.zqproject.ui.videoResource

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.ui.createmeeting.CreateMeetingViewModel
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.StatefulView

class ManageResourcesViewModel : BaseViewModel() {

    // 是否处于全选状态
    val isSelectedAll: MutableLiveData<Boolean> by lazy {
        val liveData = MutableLiveData<Boolean>()
        liveData.value = false
        liveData
    }

    val videoResources: MutableLiveData<List<StatefulView<RecordFilesInfo.RecordFile>>> by lazy {
        val data = arrayListOf<StatefulView<cn.com.ava.lubosdk.entity.RecordFilesInfo.RecordFile>>()
        val liveData = MutableLiveData<List<StatefulView<RecordFilesInfo.RecordFile>>>()
        liveData.value = data
        liveData
    }

    val mSelectedVideos: MutableList<RecordFilesInfo.RecordFile> by lazy {
        arrayListOf()
    }

    fun selectedAllOrCancel(isCancel: Boolean) {
        val list = videoResources.value
        mSelectedVideos.clear()
        list?.forEach {
            it.isSelected = !isCancel
            if (isCancel == false) {
                mSelectedVideos.add(it.obj)
            }
        }
        videoResources.value = list
        isSelectedAll.value = !isCancel
    }

    fun addOrDelSelectedVideo(video: RecordFilesInfo.RecordFile) {
        if (mSelectedVideos?.contains(video) == true) {
            mSelectedVideos.remove(video)
            logd("已包含，从队列中删除")
        } else {
            mSelectedVideos?.add(video)
            logd("添加到队列")
        }
    }
}