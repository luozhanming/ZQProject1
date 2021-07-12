package cn.com.ava.lubosdk.util

import com.google.gson.Gson

object GsonUtil {

    private val mGson: Gson = Gson()

    fun <T> fromJson(json: String, clazz: Class<T>):T {
        return mGson.fromJson(json,clazz)
    }


}