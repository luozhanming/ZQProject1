package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 设置互动视频源画面
 * */
class SetVideoWindowControl(
    val windows:List<Int>,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {

    override val name: String
        get() = "SetVideoWindow"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "112"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["court"] = windows.size.toString()
            windows.forEachIndexed{i,window->
                this["a$window"] = "a$window"
            }
        }
    }


}