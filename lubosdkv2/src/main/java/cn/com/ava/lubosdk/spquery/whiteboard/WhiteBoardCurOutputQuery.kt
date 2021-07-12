package cn.com.ava.lubosdk.spquery.whiteboard

import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.manager.LoginManager
import com.blankj.utilcode.util.EncryptUtils
/**
 * 获取当前在窗口中显示输出的白板
 * */
class WhiteBoardCurOutputQuery(override var onResult: (QueryResult) -> Unit,
                               override var onError: ((Throwable) -> Unit)? = null)
    : ISPQuery<ListWrapper<String>> {
    override val name: String
        get() = "WhiteBoardCurOutputQuery"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "6"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pwsd"] = EncryptUtils.encryptMD5ToString(LoginManager.getLogin()?.password).toLowerCase();

            this["command"] = "1"
            this["data"] = "WhiteBoard_getPenOutput"
        }
    }

    override fun build(response: String): ListWrapper<String> {
        if (response.startsWith("WhiteBoardRet_getPenOutput")) {
            val state: String = response.substring(response.indexOf("=") + 1).trim()
            val split = state.split("_").toTypedArray()
            val outputNum = split[1].toInt()
            val list = mutableListOf<String>()
            for (i in 0 until outputNum) {
                val id = split[2 + i]
                list.add(id)
            }
            return ListWrapper(list)
        }else{
            throw IllegalStateException("Query $name failed.")
        }
    }
}