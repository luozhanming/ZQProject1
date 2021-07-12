package cn.com.ava.lubosdk.control

import android.text.TextUtils
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.KeyInvalidException
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.EncryptUtil

class VGALockControl(
    val isLock: Boolean,
    override var onResult: (Boolean) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IControl {


    override val name: String
        get() = "VGALock"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["Action"] = "16433"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pssword"] =
                EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "")
                    .toLowerCase()
            this["lock"] = if (isLock) "1" else "0"
        }
    }

    override fun build(response: String): Boolean {
        if(response == "ErrorCode=2"){
            throw KeyInvalidException()
        }
        return !TextUtils.isEmpty(response) && response.contains("ErrorCode=0")
    }
}