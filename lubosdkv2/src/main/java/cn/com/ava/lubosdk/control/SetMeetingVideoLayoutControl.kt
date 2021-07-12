package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 会议模式设置布局
 * */
class SetMeetingVideoLayoutControl(
    val streamCount: Int,/*新的布局窗口数*/
    val preLayout: List<Int>,/*以前的布局（内容为互动成员号)*/
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {

    override val name: String
        get() = "SetMeetingVideoLayout"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "159"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["layout"] = streamCount.toString()
            for (i in 0 until streamCount) {
                if (preLayout.size > i) {
                    this["sid$i"] = preLayout[i].toString()
                } else {
                    this["sid$i"] = "-1"
                }
            }
        }
    }


}