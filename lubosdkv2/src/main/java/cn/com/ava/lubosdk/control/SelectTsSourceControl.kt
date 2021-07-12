package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager


/**
 * （主讲）互动中选择教师/学生视频源的镜头
 * */
class SelectTsSourceControl(
    val tSelect:Int,/*教师*/
    val sSelect:Int,/*学生*/
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {

    override val name: String
        get() = "SaveVideoPresetPos"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "110"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["tsource"] = if (tSelect == -1) "null" else tSelect.toString()
            this["sSource"] = if (sSelect == -1) "null" else sSelect.toString()
        }
    }
}