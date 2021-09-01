package cn.com.ava.zqproject.common

import android.content.Context
import cn.com.ava.zqproject.AppConfig
import com.blankj.utilcode.util.Utils
import com.tencent.mmkv.MMKV
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
        //已选择的按键
        const val KEY_SELECTED_COMMAND_KEY = "Selected_Key"
        //平台登录后使用的token
        const val KEY_PLATFORM_TOKEN = "Platform_Token"
        //平台最后一个登录的用户，没主动登出
        const val KEY_PLATFORM_LATEST_LOGIN = "Platform_Latest_Login"


        val pref = MMKV.mmkvWithID(SP_COMMON_NAME,MMKV.SINGLE_PROCESS_MODE,AppConfig.MMKV_CRYPT_KEY)




        fun <T> putElement(keyName: String, value: T) {
            pref.apply {
                when (value) {
                    is String -> encode(keyName, value)
                    is Boolean -> encode(keyName, value)
                    is Int -> encode(keyName, value)
                    is Long -> encode(keyName, value)
                    is Float -> encode(keyName, value)
                    else -> throw java.lang.IllegalArgumentException("Type Error, cannot be saved!")
                }
            }
        }

        fun <T> getElement(keyName: String, default: T): T = with(pref) {
            val res = when (default) {
                is String -> decodeString(keyName, default)
                is Boolean -> decodeBool(keyName, default)
                is Int -> decodeInt(keyName, default)
                is Long -> decodeLong(keyName, default)
                is Float -> decodeFloat(keyName, default)
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