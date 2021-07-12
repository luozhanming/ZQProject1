package cn.com.ava.lubosdk.spquery

import android.text.TextUtils
import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.LastCallResult
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.manager.LoginManager
import java.util.*
/**
 * 最近联系
 * */
class LastCallUserQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : ISPQuery<ListWrapper<LastCallResult.LastCallUser>> {


    override val name: String
        get() = "LastCallUser"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "124"
            this["key"] = LoginManager.getLogin()?.key ?: ""
        }
    }

    override fun build(response: String): ListWrapper<LastCallResult.LastCallUser> {
        val split = response.split("\\|".toRegex())
        val users: MutableList<LastCallResult.LastCallUser> =
            ArrayList<LastCallResult.LastCallUser>()
        if ("0" == split[0]) {
            val split1 = split[1].split(";".toRegex()).dropLastWhile { TextUtils.isEmpty(it) }
            for (s in split1) {
                val user = LastCallResult.LastCallUser()
                user.setUsername(s)
                users.add(user)
            }
        }
        return ListWrapper(users)
    }
}