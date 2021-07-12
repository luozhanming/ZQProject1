package cn.com.ava.lubosdk.spquery

import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.RSAKey
import cn.com.ava.lubosdk.util.GsonUtil
/**
 * 获取RSAkey
 * */
class RSAKeyQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : ISPQuery<RSAKey> {

    override val name: String
        get() = "RSAKey"

    override fun getQueryParams(): LinkedHashMap<String, String> {
       return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "5"
        }
    }

    override fun build(response: String): RSAKey {
        if (response.startsWith("{") && response.endsWith("}")
            && response.contains("modulus") && response.contains("exponent")
        ) {
            return GsonUtil.fromJson(response, RSAKey::class.java)
        } else return RSAKey()
    }
}