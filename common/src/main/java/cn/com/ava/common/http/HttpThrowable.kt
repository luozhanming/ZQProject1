package cn.com.ava.common.http


/**
 * 未知错误
 */
const val UNKNOWN = 1000

/**
 * 解析错误
 */
const val PARSE_ERROR = 1001

/**
 * 网络错误
 */
const val NETWORD_ERROR = 1002

/**
 * 协议出错
 */
const val HTTP_ERROR = 1003

/**
 * 证书出错
 */
const val SSL_ERROR = 1005

/**
 * 连接超时
 */
const val TIMEOUT_ERROR = 1006

class ResponseThrowable(throwable: Throwable, val code:Int):Exception(throwable){
    override var message:String?=null
}

class ServerException(val code: Int,message:String):RuntimeException()