package cn.com.ava.zqproject.vo

/**
 * 平台登录返回
 */
data class PlatformLogin(
    val id: String,
    val loginCount: Int,
    val name: String,
    val token: String
)
