package cn.com.ava.zqproject.vo

/**
 * 平台接口地址
 */
data class PlatformSetting(
    val addressListUrl: String,
    val appLatestVersionUrl: String,
    val askForVideoExtraUrl: String,
    val calledMeetingUrl: String,
    val communicationGroupDeleteUrl: String,
    val communicationGroupEditUrl: String,
    val communicationGroupItemUrl: String,
    val communicationGroupListUrl: String,
    val createMeetingUrl: String,
    val ftpInfo: String,
    val heartBeatUrl: String,
    val loginHtmlUrl: String,
    val platformId: String,
    val recentCallUrl: String,
    val refreshTokenUrl: String
)


//{
//    appLatestVersionUrl:"[String]获取最新版本信息",
//    communicationGroupDeleteUrl:"[String]删除通讯录群组接口",
//    recentCallUrl:"[String]最近呼叫列表",
//    loginHtmlUrl:"[String]登录页面地址",
//    refreshTokenUrl:"[String]更换用户token",
//    ftpInfo:"[String]FTPInfo",
//    calledMeetingUrl:"[String]听讲获取会议呼叫列表接口",
//    askForVideoExtraUrl:"[String]询问是否上传视频需要携带特殊信息接口，如需要则返回页面地址",
//    platformId:"[String]平台ID",
//    communicationGroupListUrl:"[String]获取通讯录群组列表接口",
//    heartBeatUrl:"[String]心跳URL",
//    createMeetingUrl:"[String]创建会议接口",
//    addressListUrl:"[String]获取平台通讯录接口",
//    communicationGroupItemUrl:"[String]获取通讯录群组详情接口",
//    communicationGroupEditUrl:"[String]创建通讯录群组接口"
//}
