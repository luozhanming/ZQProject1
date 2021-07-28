package cn.com.ava.zqproject.vo
/**
 * 通讯群组
 * */
data class ContractGroup(
    val name: String,
    val id: String,
    val username: String,
    val status: Int,
    val createTime: String,
    val userId: String,
    val statusText: String,
    val userIdList: List<ContractUser>
)




//{
//    createTime:"[Date]创建时间",
//    userIdList:"[List]群组用户列表",
//    statusText:"[String]?",
//    name:"[String]名称",
//    id:"[String]id",
//    userName:"[String]所属用户name",
//    userId:"[String]所属用户",
//    status:"[int]状态 {0=禁用 1=有效 }"
//}
