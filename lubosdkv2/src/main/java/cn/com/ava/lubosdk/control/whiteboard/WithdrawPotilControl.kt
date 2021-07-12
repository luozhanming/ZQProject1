package cn.com.ava.lubosdk.control.whiteboard

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import com.blankj.utilcode.util.EncryptUtils

/**
 * 撤回笔记
 * */
class WithdrawPotilControl(
        val classId: String,/*课室id*/
        val penType: Int,/*笔类型*/
        val count: Int,/*笔数量*/
        override var onResult: (Boolean) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null) : IControl {


    override val name: String
        get() = "TurnOnWhiteBoard"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "6"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pwsd"] = EncryptUtils.encryptMD5ToString(LoginManager.getLogin()?.password).toLowerCase();

            this["command"] = "1"
            this["data"] = "WhiteBoard_WithdrawPenData_${classId}_${penType}_$count"
        }
    }

    override fun build(response: String): Boolean {
        if ("error" in response) return false
        else {
            val split = response.split("=")
            return split.size > 1 && "ok" == split[1]
        }
    }

}