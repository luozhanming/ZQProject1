package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.entity.LocalVideoStream
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 设置会议互动本地画面
 * */
class SetMeetingLocalSceneControl(
    val isMain: Boolean,/*是否主流输出*/
    val stream: LocalVideoStream,/*视频源流*/
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
):IControl {

    override val name: String
        get() = "SetMeetingLocalScene"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = if(isMain) "109" else "111"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["source"] =stream.windowIndex.toString()
        }
    }
}