package cn.com.ava.common.util

import android.util.ArrayMap
import android.util.SparseArray
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    private val dateFormats:ArrayMap<String,DateFormat> by lazy {
        ArrayMap<String,DateFormat>()
    }


    fun toDateString( time:Long,format: String): String {
        var get = dateFormats.get(format)
        if(get==null){
            get = SimpleDateFormat(format)
            dateFormats.put(format,get)
        }
        val format1 = get.format(Date(time))
        return format1
    }


    fun toTimeStamp( source:String,format: String):Long{
        var get = dateFormats.get(format)
        if(get==null){
            get = SimpleDateFormat(format)
            dateFormats.put(format,get)
        }
        val format1 = get.parse(source)
        return format1.time
    }
}