package cn.com.ava.zqproject.vo

data class ContractUser(
    val userId: String,
    val userName: String,/*名字*/
    val professionTitleName: String/*职位名称*/,
    val userProfessionTitle:String
) {
    override fun equals(other: Any?): Boolean {
        return userId == (other as? ContractUser)?.userId
    }
}
