package cn.com.ava.lubosdk.spquery.whiteboard

import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.whiteboard.BlankBoardState
import cn.com.ava.lubosdk.manager.LoginManager
import com.blankj.utilcode.util.EncryptUtils


/**
 * 查询白板状态
 * */
class BlankBoardStateQuery(override var onResult: (QueryResult) -> Unit,
                           override var onError: ((Throwable) -> Unit)? = null) : ISPQuery<BlankBoardState> {
    override val name: String
        get() = "BlankBoardState"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "6"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pwsd"] = EncryptUtils.encryptMD5ToString(LoginManager.getLogin()?.password).toLowerCase();
            this["command"] = "1"
            this["data"] = "WhiteBoard_getStatus"
        }
    }

    override fun build(response: String): BlankBoardState {
        if (response.contains("GetDataError")) throw IllegalStateException("$name GetDataError")
        else {
            val state = BlankBoardState()
            val split = response.substring(response.indexOf("=") + 1).trim().split("_")
            state.isLaunch = split[0] == "1"
            state.mode = split[1].toInt()
            state.isBgSwitch = split[2] == "1"
            state.isWriting = split[4]=="1"
            state.isHasAnswer = split[5]=="1"
            state.clientType = split[6].toInt()
            return state
        }
    }
}