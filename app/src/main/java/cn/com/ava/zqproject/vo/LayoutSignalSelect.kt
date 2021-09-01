package cn.com.ava.zqproject.vo

import cn.com.ava.lubosdk.entity.LinkedUser

/**
 * 画面布局信号选择
 * */
data class LayoutSignalSelect(
    var signalIndex: Int,
    var selectedMemeber: LinkedUser?,
   // var unselectedMember: List<LinkedUser>?
)