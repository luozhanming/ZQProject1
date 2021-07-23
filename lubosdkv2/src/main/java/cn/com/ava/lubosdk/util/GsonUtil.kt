package cn.com.ava.lubosdk.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object GsonUtil {

    private val mGson: Gson = Gson()

    fun <T> fromJson(json: String, clazz: Class<T>):T {
        return mGson.fromJson(json,clazz)
    }

    fun <T> fromJson(json: String, type: Type):T {

        return mGson.fromJson(json,type)
    }

    fun <T> toJson(src:T):String{
        return mGson.toJson(src)
    }


}