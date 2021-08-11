package cn.com.ava.zqproject.vo

data class AppUpgrade(
    val forceUpdated: Boolean,
    val platformIdentify: Int,
    val remark: Int,
    val logoUri: String?,
    val packageDFSId: String,
    val versionName: String,
    val packageDFSUri: String?,
    val version: Int,
    val packageDownload: String,
    val name: String,
    val logo: String,
    val id: String,
    val packageName: String
)


//forceUpdated:"[boolean]是否强制更新",
//platformIdentify:"[int]运行平台标识 {0=未知 1=Android 2=ios }",
//remark:"[String]更新说明",
//logoUri:"[String]?",
//packageDFSId:"[String]更新包",
//versionName:"[String]版本名",
//packageDFSUri:"[String]?",
//version:"[Integer]版本号",
//packageDownload:"[String]IOS下载地址",
//createTime:"[Date]创建时间",
//name:"[String]名称",
//logo:"[String]logo",
//id:"[String]id",
//packageName:"[String]?",
//softwareIdentity:"[int]软件标志 {0=党建APP 1=TP软件 }",
//platformIdentifyText:"[String]?",
//softwareIdentityText:"[String]?"
