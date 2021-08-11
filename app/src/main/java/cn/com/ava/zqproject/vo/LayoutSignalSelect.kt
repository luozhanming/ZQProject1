package cn.com.ava.zqproject.vo

/**
 * 画面布局信号选择
 * */
data class LayoutSignalSelect(
    val signalIndex: Int,
    val selectedMemeber: String,
    val unselectedMember: List<String>
)