package cn.com.ava.zqproject.vo

import java.io.Serializable

/**
 * 振铃信息
 * */
data class InvitationInfo(
    val confNo: String,
    val enableWaitingRoom: Boolean,
    val initiatorName: String,
    val ticket: String,
    val title: String
):Serializable