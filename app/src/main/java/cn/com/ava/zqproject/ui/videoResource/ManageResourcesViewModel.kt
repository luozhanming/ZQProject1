package cn.com.ava.zqproject.ui.videoResource

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cn.com.ava.base.ui.BaseViewModel
import cn.com.ava.common.util.GsonUtil
import cn.com.ava.common.util.logPrint2File
import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.manager.VideoResourceManager
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.createmeeting.CreateMeetingViewModel
import cn.com.ava.zqproject.ui.videoResource.service.VideoSingleton
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ManageResourcesViewModel : BaseViewModel() {

    // 是否处于全选状态
    val isSelectedAll: MutableLiveData<Boolean> by lazy {
        val liveData = MutableLiveData<Boolean>()
        liveData.value = false
        liveData
    }

    val videoResources: MutableLiveData<MutableList<StatefulView<RecordFilesInfo.RecordFile>>> by lazy {
        val data = arrayListOf<StatefulView<cn.com.ava.lubosdk.entity.RecordFilesInfo.RecordFile>>()
        val liveData = MutableLiveData<MutableList<StatefulView<RecordFilesInfo.RecordFile>>>()
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

    // 获取本地缓存的视频
//    fun getCacheVideos(): ArrayList<RecordFilesInfo.RecordFile> {
//        return VideoSingleton.getInstance().cacheVideos


//        val cacheVideos = VideoPreference.getElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, "")
//        var cacheList = arrayListOf<RecordFilesInfo.RecordFile>()
//        if (cacheVideos != null && cacheVideos.length != 0) {
//            cacheList = Gson().fromJson<ArrayList<RecordFilesInfo.RecordFile>>(cacheVideos, object : TypeToken<ArrayList<RecordFilesInfo.RecordFile>>(){}.type)
//        }
////        logd("本地视频: ${cacheList.toString()}")
//        return cacheList
//    }

    // 检测是否有缓存
//    fun checkCacheResult(data: RecordFilesInfo.RecordFile): Boolean {
//        var cacheList = getCacheVideos()
//
////        logd("本地视频: ${cacheList.toString()}")
//
//        cacheList.forEach {
//            if (data.downloadUrl == it.downloadUrl) {
////                if (it.downloadProgress < 100) {
////                    ToastUtils.showShort("该视频正在下载中")
////                } else {
////                    ToastUtils.showShort("该视频已下载完成")
////                }
//                return true
//            }
//        }
//        return false
//    }

    // 保存视频记录
//    fun saveCacheVideo(list: MutableList<RecordFilesInfo.RecordFile>) {
//        var cacheList = getCacheVideos()
//        cacheList.addAll(0, list)
//        VideoSingleton.getInstance().cacheVideos = cacheList
//        VideoPreference.putElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, GsonUtil.toJson(cacheList))
//    }
//
//    // 删除视频
//    fun deleteVideo(video: RecordFilesInfo.RecordFile) {
//        mDisposables.add(
//            VideoResourceManager.deleteRecordFile(video)
//                .compose(PlatformApi.applySchedulers())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ result ->
//                    if (result == true) {
//                        logd("删除视频资源成功")
//                        if (mSelectedVideos?.contains(video) == true) {
//                            mSelectedVideos.remove(video)
//                        }
//                        if (mSelectedVideos.size == 0) {
//                            ToastUtils.showShort("删除成功")
//                        }
//                        val videos = videoResources.value
//                        val stateful = StatefulView(video)
//                        videos?.remove(stateful)
//                        videoResources.value = videos
//                    } else {
//                        ToastUtils.showShort("删除失败")
//                        logd("删除视频资源失败")
//                    }
//                }, {
//                    logPrint2File(it)
//                    logd("删除视频资源接口出错")
//                })
//        )
//    }
}