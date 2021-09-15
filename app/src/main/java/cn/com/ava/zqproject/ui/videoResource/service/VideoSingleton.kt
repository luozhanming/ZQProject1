package cn.com.ava.zqproject.ui.videoResource.service

import cn.com.ava.lubosdk.entity.RecordFilesInfo

class VideoSingleton private constructor() {

//    public var cacheVideos: ArrayList<RecordFilesInfo.RecordFile> = arrayListOf()

    companion object {
        private var instances: VideoSingleton? = null

        fun getInstance(): VideoSingleton {
            if (instances == null) {
                synchronized(this) {
                    if (instances == null) {
                        instances = VideoSingleton()
                    }
                }
            }
            return instances!!
        }
    }

}