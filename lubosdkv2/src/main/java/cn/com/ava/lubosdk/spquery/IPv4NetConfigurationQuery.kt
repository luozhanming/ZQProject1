package cn.com.ava.lubosdk.spquery

import android.text.TextUtils
import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.entity.IPv4NetConfiguration
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.manager.LoginManager

/**
 * 录播网络设置查询
 * */
class IPv4NetConfigurationQuery(override var onResult: (QueryResult) -> Unit,
                                override var onError: ((Throwable) -> Unit)? = null) : ISPQuery<IPv4NetConfiguration> {
    override val name: String
        get() = "IPv4NetConfiguration"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "80"
            this["key"] = LoginManager.getLogin()?.key ?: ""
        }
    }

    override fun build(response: String): IPv4NetConfiguration {
        val split = response.split("|").dropLastWhile { TextUtils.isEmpty(it) }.toTypedArray()
        val ip = split[1]
        val mask = split[2]
        val gateway = split[3]
        val macAddress = split[4]
        val dns = split[10]
        val backupDNS = split[11]
        val configuration = IPv4NetConfiguration()
        configuration.ip = ip
        configuration.mask = mask
        configuration.gateway = gateway
        configuration.macAddress = macAddress
        configuration.masterDNS = dns
        configuration.subDNS = backupDNS
        return configuration
    }
}