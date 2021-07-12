package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.KeyInvalidException
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil

/**
 * Sip课堂推送
 * */
class SipPushControl(
        val ip: String,/*ip*/
        val courseId: String,/*课堂音视频id*/
        val csTitle: String,/*课堂名称*/
        val startTime: String,/*开始时间*/
        val endTime: String,/*结束时间*/
        val csTeacher: String,/*老师名称*/
        val isTeacher: Boolean,/*是否主讲*/
        val ridCount: Int,/*参与者数量*/
        val ridIds: List<String>,/*参与者id*/
        val ridNames: List<String>,/*参与者名字*/
        override var onResult: (Boolean) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null) : IControl {


    override val name: String
        get() = "SipPush"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["Action"] = "16440"
            this["username"] = LoginManager.getLogin()?.username ?: ""
            this["password"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password
                    ?: "").toLowerCase()
            this["ip"] = EncryptUtil.bytes2HexString(ip.toByteArray()).toLowerCase()
            this["courseVid"] = courseId
            this["csTitle"] = EncryptUtil.bytes2HexString(csTitle.toByteArray()).toLowerCase()
            this["startTime"] = EncryptUtil.bytes2HexString(startTime.toByteArray()).toLowerCase()
            this["endTime"] = EncryptUtil.bytes2HexString(endTime.toByteArray()).toLowerCase()
            this["csTeacher"] = EncryptUtil.bytes2HexString(csTeacher.toByteArray()).toLowerCase()
            this["mode"] = if (isTeacher) "4" else "5"
            this["Rids_count"] = ridCount.toString()
            val size = ridIds.size
            for (i in 1..size) {
                this["rid$i"] = ridIds[i - 1]
                this["rName$i"] = EncryptUtil.bytes2HexString(ridNames[i - 1].toByteArray())
            }
        }
    }

    override fun build(response: String): Boolean {
        if(response == "ErrorCode=2"){
            throw KeyInvalidException()
        }
        return "ErrorCode=0" in response
    }
}