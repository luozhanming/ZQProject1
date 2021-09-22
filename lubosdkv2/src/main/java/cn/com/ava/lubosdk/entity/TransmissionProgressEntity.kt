package cn.com.ava.lubosdk.entity

data class TransmissionProgressEntity(
    var transmissionType: Int, // 1-下载，2-上传
    var progress: String,  // 传输进度
    var state: Int  // 状态，0-等待，1-成功，2-进行中，3-失败
)
