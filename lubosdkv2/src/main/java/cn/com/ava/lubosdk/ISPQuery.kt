package cn.com.ava.lubosdk

import cn.com.ava.lubosdk.entity.QueryResult

interface ISPQuery<T:QueryResult>{

    val name:String

    var onResult:(QueryResult)->Unit

    var onError:((Throwable)->Unit)?


    fun getQueryParams():LinkedHashMap<String,String>



    fun build(response:String):T


}