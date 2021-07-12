package cn.com.ava.lubosdk

import cn.com.ava.lubosdk.entity.QueryResult

interface IQuery<T:QueryResult> {


    val name:String

    /**
     * 请求的参数
     * */
    val mQueryFields:Array<Int>

    var isResume:Boolean

    /**
     * 请求完后所需请求结果的偏移
     * */
    var offsets:Int

    var onResult:(QueryResult)->Unit

    var onError:((Throwable)->Unit)?




    fun isNeedSystemTime():Boolean =false

    fun resultSize():Int = mQueryFields.size


    fun build(result:List<String>):T



}