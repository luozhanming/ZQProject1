package cn.com.ava.zqproject.vo

data class PlatformResponse<T>(
    val code: Int,
    val data: T,
    val message: String,
    val sysTime: Long,
    val sysTimeTip: String,
    val query:Query,
    val success:Boolean
)