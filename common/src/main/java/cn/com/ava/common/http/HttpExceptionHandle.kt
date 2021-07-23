package cn.com.ava.common.http

import cn.com.ava.common.R
import com.blankj.utilcode.util.Utils
import com.google.gson.JsonParseException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.text.ParseException

object HttpExceptionHandle {
    const val UNAUTHORIZED = 401
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404
    const val REQUEST_TIMEOUT = 408
    const val INTERNAL_SERVER_ERROR = 500
    const val BAD_GATEWAY = 502
    const val SERVICE_UNAVAILABLE = 503
    const val GATEWAY_TIMEOUT = 504


    fun handleException(e: Throwable): ResponseThrowable {
        var ex: ResponseThrowable
        if (e is HttpException) {
            ex = ResponseThrowable(e, HTTP_ERROR)
            ex.message = Utils.getApp().getString(R.string.http_net_error)
            return ex
        } else if (e is ServerException) {
            ex = ResponseThrowable(e, e.code)
            ex.message = e.message
        }else if(e is JsonParseException||e is JSONException ||e is ParseException){
            ex = ResponseThrowable(e, PARSE_ERROR)
            ex.message = Utils.getApp().getString(R.string.http_parse_error_msg)
        }else if(e is ConnectException){
            ex = ResponseThrowable(e, NETWORD_ERROR)
            ex.message = Utils.getApp().getString(R.string.http_connect_failed)
        }else if(e is ConnectTimeoutException||e is SocketTimeoutException){
            ex = ResponseThrowable(e, TIMEOUT_ERROR)
            ex.message = Utils.getApp().getString(R.string.http_timeout_msg)
        }else{
            ex = ResponseThrowable(e, UNKNOWN)
            ex.message = Utils.getApp().getString(R.string.http_unknown_msg)
        }
        return ex
    }


}

