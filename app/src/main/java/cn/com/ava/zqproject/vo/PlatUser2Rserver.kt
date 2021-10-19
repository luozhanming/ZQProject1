package cn.com.ava.zqproject.vo
/**
 * 平台用户Rserver映射
 * */
data class PlatUser2Rserver(
    val createTime: String,
    val id: String,
    val platformId: String?,
    val rsAcct: String?,
    val status: Int,
    val statusText: String,
    val userId: String
)
