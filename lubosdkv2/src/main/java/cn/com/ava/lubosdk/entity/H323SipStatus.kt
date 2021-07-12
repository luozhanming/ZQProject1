package cn.com.ava.lubosdk.entity

class H323SipStatus(
        var h323RegStatus: Int = 0,/*h323注册状态。0=注册失败，1=注册成功，2=取消注册成功*/
        var sipRegStatus: Int = 0,/*sip注册状态。0=注册失败，1=注册成功，2=取消注册成功*/
        var tlsEnable: Boolean = false,/*tls开关*/
        var srtpEnable: Boolean = false,/*srtp开关*/
        var shareDualStreamStatus: Int = 0,/*分享双流状态。1=分享中，0=结束分享*/
        var h323RegisterInfo: H323RegisterInfo?=null,/*h323注册信息*/
        var sipRegisterInfo: SipRegisterInfo?=null,/*sip注册信息*/
        var nickname: String = ""
) : QueryResult {

    class H323RegisterInfo(
            var gatekeeper: String = "",/*网守地址*/
            var h323Alias: String = "",/*H323别名*/
            var h323Ext: String = "" /*H323分机*/
    )


    class SipRegisterInfo(
            var username: String = "",
            var proxyAddr: String = "",/*代理服务器地址*/
            var registerAddr: String = "",/*注册服务器地址*/
            var authentication: String = "",/*鉴权名称*/
            var password: String = ""/*密码*/
    )


}
