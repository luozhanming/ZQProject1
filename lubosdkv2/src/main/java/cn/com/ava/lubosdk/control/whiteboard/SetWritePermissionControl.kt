package cn.com.ava.lubosdk.control.whiteboard

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.entity.whiteboard.ClassroomControlEntity
import cn.com.ava.lubosdk.manager.LoginManager
import com.blankj.utilcode.util.EncryptUtils
/**
 * 课室书写权限设置
 * */
class SetWritePermissionControl(val entity: ClassroomControlEntity,
                                val isOn: Boolean,
                                override var onResult: (Boolean) -> Unit,
                                override var onError: ((Throwable) -> Unit)? = null) : IControl {
    override val name: String
        get() = "SetWritePermissionControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "6"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pwsd"] = EncryptUtils.encryptMD5ToString(LoginManager.getLogin()?.password).toLowerCase();

            this["command"] = "1"
            this["data"] = "WhiteBoard_SetMask_${if (isOn) "open" else "close"}_${entity.id}"
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