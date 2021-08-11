package cn.com.ava.zqproject.ui.videoResource

import cn.com.ava.zqproject.AppConfig
import com.tencent.mmkv.MMKV
import kotlin.reflect.KProperty

class VideoPreference<T>(private val keyName: String, private val default: T) {

    companion object{
        // MMKV的ID
        const val MMKVID = "VideoMMKVID"
        // 视频传输列表
        const val KEY_VIDEO_TRANSMISSION_LIST = "Video_Transmission_List"

        val pref = MMKV.mmkvWithID(MMKVID, MMKV.SINGLE_PROCESS_MODE, AppConfig.MMKV_CRYPT_KEY)

        fun <T> putElement(keyName: String, value: T) {
            pref.apply {
                when (value) {
                    is String -> encode(keyName, value)
                    is Boolean -> encode(keyName, value)
                    is Int -> encode(keyName, value)
                    is Long -> encode(keyName, value)
                    is Float -> encode(keyName, value)
                    else -> throw IllegalArgumentException("Type Error, cannot be saved!")
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
                else -> throw  IllegalArgumentException("Type Error, cannot be saved!")
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