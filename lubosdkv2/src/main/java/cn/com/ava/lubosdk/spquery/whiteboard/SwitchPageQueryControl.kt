package cn.com.ava.lubosdk.spquery.whiteboard

import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.whiteboard.PageInfo
import cn.com.ava.lubosdk.manager.LoginManager
import com.blankj.utilcode.util.EncryptUtils

class SwitchPageQueryControl(
        val page: Int,
        override var onResult: (QueryResult) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null) : ISPQuery<PageInfo> {
    override val name: String
        get() = "SwitchPageQueryControl"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "6"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pwsd"] = EncryptUtils.encryptMD5ToString(LoginManager.getLogin()?.password).toLowerCase();

            this["command"] = "1"
            this["data"] = "WhiteBoard_SwitchPage_$page"
        }
    }

    override fun build(response: String): PageInfo {
        val info = PageInfo()
        val split = response.split("_").toTypedArray()
        info.curPage = split[0].toInt()
        val list = mutableListOf<Int>()
        split.drop(1).forEach {
            list.add(it.toInt())
        }
        info.pages = list
        return info
    }
}