package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager


/**
 * 展示预览画面字幕
 * */
class ShowCaptionControl(
    val isShow:Boolean,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {


    override val name: String
        get() = "ShowCaption"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "171"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["idx"] = "0"
            this["visiable"] = if(isShow)"show" else "hide"
        }
    }
}