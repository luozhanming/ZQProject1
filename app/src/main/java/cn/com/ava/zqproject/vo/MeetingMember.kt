package cn.com.ava.zqproject.vo

import cn.com.ava.lubosdk.entity.LinkedUser
/**
 * 入会成员实体
 * */
data class MeetingMember(
    val user: LinkedUser,
    var meetingNickname: String,
    var isAudioEnable: Boolean,
    var isVideoEnable: Boolean
)
