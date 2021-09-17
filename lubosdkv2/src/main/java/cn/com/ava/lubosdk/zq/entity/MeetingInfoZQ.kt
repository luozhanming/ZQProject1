package cn.com.ava.lubosdk.zq.entity

import cn.com.ava.lubosdk.entity.QueryResult

data class MeetingInfoZQ(
    var isDualMeeting: Boolean = false,/*是否双流会议*/
    var interaProxyMode: Boolean = false,/*是否内置云会议*/
    var confMaxShowNum: Int =-1,/*最大会议人数*/
    var sipCourseMode: Int=0,/*1=云南sip跨域模式，0=普通模式*/
    var waitingRoomEnable: Boolean = false,/*等候室开关，0=关，1=开*/
    var confMode: String = "cloudCtrlMode",/*teachMode=授课模式，confMode=会议模式，classMode=听课模式，recordMode=录课模式，cloudCtrlMode=云会控模式*/
    var confStatus: String = "destroyed",/*created=会议中，destroyed=会议结束*/
    var confType: String="cloudCtrl",/*会议类型（conference=会议模式，teach=授课模式，cloudCtrl=云会控模式）*/
    var confTheme: String = "",/*会议信息，包括会议号 会议密码 会议主题*/
    var confId:String = "",/*会议号*/
    var confpsw:String = "",  /*会议密码*/
    var confStartTime: String = "",/*会议开始时间（格式：yyyy-mm-dd hh:nn:ss，16进制编码）*/
    var confErrorCode: String=""


):QueryResult