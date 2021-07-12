package cn.com.ava.lubosdk.control.whiteboard

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.entity.whiteboard.ClassroomControlEntity
import cn.com.ava.lubosdk.manager.LoginManager
import com.blankj.utilcode.util.EncryptUtils

class PushWhiteBoardControl(
        val entities: List<ClassroomControlEntity>,
        override var onResult: (Boolean) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null) : IControl {


    override val name: String
        get() = "PushWhiteBoardControl"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "6"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pwsd"] = EncryptUtils.encryptMD5ToString(LoginManager.getLogin()?.password).toLowerCase();

            this["command"] = "1"
            val map = entities.map {
                it.id
            }
            var data: String = ""
            for (s in map) {
                data += "${s}_"
            }
            data = data.dropLast(0)
            this["data"] = "WhiteBoard_penOutput_$data"
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