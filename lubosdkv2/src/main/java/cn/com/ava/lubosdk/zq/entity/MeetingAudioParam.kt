package cn.com.ava.lubosdk.zq.entity

import cn.com.ava.lubosdk.entity.QueryResult

/**
 * 会议音频参数
 * */
data class MeetingAudioParam(
    var muteAllState:String="",/*静音状态  0不静音  1全场静音*/
    var muteStatus:String=""/*本机用户numberid*/

):QueryResult
