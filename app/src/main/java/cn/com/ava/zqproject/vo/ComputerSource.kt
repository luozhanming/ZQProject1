package cn.com.ava.zqproject.vo

/**
 * 电脑视频源
 * */
data class ComputerSource(
    val name: String,
    val topWindowIndex: Int = -1,
    val subWindowIndex: Int = -1,
    val isSubSource: Boolean,
    val isCurOutput:Boolean
    )
