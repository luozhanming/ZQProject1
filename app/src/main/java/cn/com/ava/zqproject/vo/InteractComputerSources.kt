package cn.com.ava.zqproject.vo
/**
 * 互动电脑的视频源集合
 * */
data class InteractComputerSources(val computerIndex:Int,/*电脑索引*/
val isHasMultiSource:Boolean,/*是否有多个源头*/
val curSourceIndex:Int,/*当前选中视频源索引*/
val sources:List<String>,/*视频源名称*/
val sourcesCmd:List<String>/*视频源指令*/)