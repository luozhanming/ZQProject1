package cn.com.ava.zqproject.ui.videoResource.service

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.zqproject.ui.videoResource.VideoPreference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VideoSingleton private constructor() {

    companion object {
        private var instances: VideoSingleton? = null

        var cacheVideos: ArrayList<RecordFilesInfo.RecordFile> = arrayListOf()

        fun getInstance(): VideoSingleton {
            if (instances == null) {
                synchronized(this) {
                    if (instances == null) {
                        instances = VideoSingleton()
                        initData()
                    }
                }
            }
            return instances!!
        }

        fun initData() {
            val cacheStr = VideoPreference.getElement(VideoPreference.KEY_VIDEO_TRANSMISSION_LIST, "")
            if (cacheStr != null && cacheStr.length != 0) {
                cacheVideos = Gson().fromJson<ArrayList<RecordFilesInfo.RecordFile>>(cacheStr, object : TypeToken<ArrayList<RecordFilesInfo.RecordFile>>(){}.type)
            }
            logd("初始化缓存的传输数据，size = ${cacheVideos.size}")
        }
    }



}