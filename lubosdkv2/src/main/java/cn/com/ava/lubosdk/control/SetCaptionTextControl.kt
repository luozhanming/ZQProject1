package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 设置画面字幕文本
 * */
class SetCaptionTextControl(
    val text:String,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {


    override val name: String
        get() = "SetCaptionText"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "171"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = "4"
            this["op"] = "text@"
            this["text"] = text
        }
    }
}