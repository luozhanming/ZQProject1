package cn.com.ava.zqproject.vo
/**
 * 平台服务返回
 * */
data class PlatformResponse<T>(
    val code: Int,
    val data: T,
    val message: String,
    val sysTime: Long,
    val sysTimeTip: String,
    val query:Query,
    val success:Boolean
)