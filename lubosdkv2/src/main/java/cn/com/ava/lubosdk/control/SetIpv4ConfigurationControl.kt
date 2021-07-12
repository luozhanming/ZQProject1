package cn.com.ava.lubosdk.control

import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager

class SetIpv4ConfigurationControl(
        val ip: String,
        val mask: String,
        val gateway: String,
        val mac: String,
        val dns: String,
        val backupDNS: String,
        val mtu:Int=1500,
        override var onResult: (Boolean) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null) : IControl {
    override val name: String
        get() = "SetIpv4Configuration"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "1"
            this["cmd"] = "81"
            this["key"] = LoginManager.getLogin()?.key ?: ""
            this["ip"] = ip
            this["mask"] = mask
            this["gateway"] = gateway
            this["mac"] = mac
            this["dns"] = dns
            this["backupDns"] = backupDNS
            this["mtu"] = mtu.toString()
        }
    }
}