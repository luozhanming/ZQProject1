package cn.com.ava.zqproject.vo
/**
* 互动电脑视频源
* */
data class InteractComputerSource(val computerIndex:Int,/*电脑索引*/
                             val isHasMultiSource:Boolean,/*是否有多个源头*/
                             val isCurSelected:Boolean,/*当前选中视频源索引*/
                             val name:String,/*视频源名称*/
                             val sourceCmd:String/*视频源指令*/)