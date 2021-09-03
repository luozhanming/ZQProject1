package cn.com.ava.lubosdk.entity.zq

/**
 * 申请发言用户
 * */
data class ApplySpeakUser(
    val numId:Int = -1,  /*会议成员id*/
    val name:String?=null,/*名字*/
    val position:String?=null/*职位*/
)
