package cn.com.ava.lubosdk.entity.zq

import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.lubosdk.entity.QueryResult

data class MeetingMemberInfo(
    val datas: List<LinkedUser>,
    val userCnt: Int,
    val localNumberId: Int,
    val localRole: Int
):QueryResult