package cn.com.ava.lubosdk.spquery

import android.text.TextUtils
import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.InteraInfo
import cn.com.ava.lubosdk.entity.LinkedUser
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.GsonUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
import java.util.*


/**
 * 获取会议互动成员信息
 * */
class InteracMemberInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : ISPQuery<InteraInfo> {
    override val name: String
        get() = "InteraInfo"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "4"
            this["cmd"] = "108"
            this["key"] = LoginManager.getLogin()?.key ?: ""
        }
    }

    override fun build(response: String): InteraInfo {
        val info = GsonUtil.fromJson(response, InteraInfo::class.java)
        val onlineList: List<LinkedUser> = info.getOnlineList() ?: return info
        val newUsers: MutableList<LinkedUser> = ArrayList<LinkedUser>()
        for (user in onlineList) {
            if (user.getShortNumer() != "0" || !TextUtils.isEmpty(user.getUsername())) {
                if(!TextUtils.isEmpty(user.nickname)){
                    user.nickname = URLHexEncodeDecodeUtil.hexToStringUrlDecode(user.nickname)
                }
                newUsers.add(user)
            }
        }
        info.setOnlineList(newUsers)
        return info
    }
}