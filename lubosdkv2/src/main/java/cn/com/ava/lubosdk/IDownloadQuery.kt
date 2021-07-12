package cn.com.ava.lubosdk

import cn.com.ava.lubosdk.entity.QueryResult
import okhttp3.ResponseBody

interface IDownloadQuery {
    val name:String

    var onResult:(Boolean)->Unit

    var onError:((Throwable)->Unit)?


    fun getQueryParams():LinkedHashMap<String,String>



    fun build(response:ResponseBody):Boolean



}