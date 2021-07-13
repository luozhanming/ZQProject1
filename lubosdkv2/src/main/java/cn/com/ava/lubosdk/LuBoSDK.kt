package cn.com.ava.lubosdk

import android.util.Log

object LuBoSDK {

    private var sIP: String? = null
    private var sPort: String? = null
    private var sHost: String? = null
    private var sEnableLog = false

    private var mListener:NetErrorListener? = null

    /**
     * 确定录播服务器时需要调用
     */
    fun init(ip: String, port: String, enableLog: Boolean) {
        reset()
        if (ip == sIP && port == sPort) return
        sHost = "http://${ip}:${port}/"
        sIP = ip
        sPort = port
        sEnableLog = enableLog
        AVAHttpEngine.init("$sIP:$sPort")
        Log.e("hahaha", "has init api")
        AVAHttpEngine.startQueryEngine()
    }
    /**
     * 重新选择录播的时候需要调用来清除缓存
     */
    fun reset() {
        sHost = null
        sEnableLog = false
        Cache.getCache().clear()
    }

    fun getHost(): String? {
        return sHost
    }

    fun setNetErrorListener(listener: NetErrorListener?) {
        mListener = listener
        AVAHttpEngine.setNetErrorListener(listener)
    }

    interface NetErrorListener {
        fun onTimeoutError()
        fun onConnectFailedError()
        fun onUnknownError()
    }
}