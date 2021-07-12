package cn.com.ava.lubosdk.spquery

import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.LastCallResult
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.GsonUtil


/**
 * 查询录播最近呼叫用户
 * */
class RecentCallUserQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : ISPQuery<ListWrapper<LastCallResult.LastCallUser>> {
    override val name: String
        get() = "RecentCallUser"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "126"
            this["key"] = LoginManager.getLogin()?.key?:""
        }
    }

    override fun build(response: String): ListWrapper<LastCallResult.LastCallUser> {
        val lastCallResult: LastCallResult = GsonUtil.fromJson(response, LastCallResult::class.java)
        return ListWrapper(lastCallResult.lastCall)
    }
}