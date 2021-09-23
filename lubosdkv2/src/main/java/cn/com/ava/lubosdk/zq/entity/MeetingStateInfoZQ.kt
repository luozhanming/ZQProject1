package cn.com.ava.lubosdk.zq.entity

import cn.com.ava.lubosdk.entity.QueryResult

/**
 * 政企会议特殊状态
 * */
data class MeetingStateInfoZQ(
    var requestSpeakStatus: List<Int>? = null,/*主讲收到的正在申请发言的听课numberId*/
    var requestSpeakRetStatus: Int = 0,/*听课收到主讲的申请发言回复，0=无回复，1=允许发言，2=不允许发言*/
    var requestSpeakMode: Int = 0,/*发言模式，0表示当前不在发言模式，大于0表示当前在发言的用户numberId*/
    var lockConference: Boolean = false,/*0=会议未锁定，1=会议锁定*/
    var localCameraCtrl: Boolean = false,/*0=本地开启摄像头，1=本地关闭摄像头*/
    var cameraCtrlState: List<Int>? = null,/*远端摄像头禁用用户的numberId*/
    var audioCtrlState: List<Int>? = null
) : QueryResult