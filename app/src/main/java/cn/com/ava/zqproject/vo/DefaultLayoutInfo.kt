package cn.com.ava.zqproject.vo
/**
 * 默认布局信息
 * */
data class DefaultLayoutInfo(
    val confId: String,
    var hasLayoutInitDefault:Boolean,   /*是否布局了默认的*/
    var isInDefaultLayout: Boolean,  /*是否停止默认布局*/
    val contractUsers: List<ContractUser>
)
