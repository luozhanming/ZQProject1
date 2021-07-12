package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
/**
 * 录制控制
 * */
class RecordControl(
    @Constant.RecordState val state: Int,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)?
) : IControl {
    override val name: String
        get() = "Record"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            if (state == Constant.RECORD_RECORDING) {
                this["cmd"] = "1001"
            } else if (state == Constant.RECORD_STOP) {
                this["cmd"] = "1002"
            } else if (state == Constant.RECORD_PAUSE) {
                this["cmd"] = "1003"
            } else if (state == Constant.RECORD_RESUME) {
                this["cmd"] = "1004"
            }
            this["key"] = LoginManager.getLogin()?.key ?: ""
        }
    }

    override fun build(response: String): Boolean {
        return "-1" !in response
    }
}