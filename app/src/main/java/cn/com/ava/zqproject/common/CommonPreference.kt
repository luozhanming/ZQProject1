package cn.com.ava.zqproject.common

import android.content.Context
import com.blankj.utilcode.util.Utils
import kotlin.reflect.KProperty

class CommonPreference<T>(private val keyName: String, private val default: T) {
    companion object{
        //SP名字
        const val SP_COMMON_NAME = "common"

        //上次登录成功录播IP
        const val KEY_LUBO_IP = "Lubo_IP"
        //上次登录成功录播端口
        const val KEY_LUBO_PORT = "Lubo_Port"
        //用户名
        const val KEY_LUBO_USERNAME = "Lubo_Username"
        //密码
        const val KEY_LUBO_PASSWORD = "Lubo_Password"
        //平台地址
        const val KEY_PLATFORM_ADDR = "Platform_Address"


        val pref = Utils.getApp().getSharedPreferences(SP_COMMON_NAME, Context.MODE_PRIVATE)


        //内存缓存
        val cache = hashMapOf<String, Any?>()

        fun <T> putElement(keyName: String, value: T) {
            pref.edit().apply {
                when (value) {
                    is String -> putString(keyName, value)
                    is Boolean -> putBoolean(keyName, value)
                    is Int -> putInt(keyName, value)
                    is Long -> putLong(keyName, value)
                    is Float -> putFloat(keyName, value)
                    else -> throw java.lang.IllegalArgumentException("")
                }
                cache[keyName] = value
            }.apply()
        }

        fun <T> getElement(keyName: String, default: T): T = with(pref) {
            val res = when (default) {
                is String -> getString(keyName, default)
                is Boolean -> getBoolean(keyName, default)
                is Int -> getInt(keyName, default)
                is Long -> getLong(keyName, default)
                is Float -> getFloat(keyName, default)
                else -> throw IllegalArgumentException("Type Error, cannot be saved!")
            }
            return res as T
        }
    }


    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getElement(keyName, default)
    }


    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putElement(keyName, value)
    }







}