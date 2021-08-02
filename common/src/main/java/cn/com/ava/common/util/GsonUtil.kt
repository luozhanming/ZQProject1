package cn.com.ava.common.util

import com.google.gson.Gson
import java.lang.reflect.Type

object GsonUtil {
    private val mGson: Gson = Gson()

    fun <T> fromJson(json: String, clazz: List<Any>):T {
        return mGson.fromJson(json,clazz)
    }

    fun <T> fromJson(json: String, type: Type):T {

        return mGson.fromJson(json,type)
    }

    fun <T> toJson(src:T):String{
        return mGson.toJson(src)
    }
}