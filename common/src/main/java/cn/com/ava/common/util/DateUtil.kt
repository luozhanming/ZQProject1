package cn.com.ava.common.util

import android.util.ArrayMap
import android.util.SparseArray
import java.sql.Timestamp
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



    fun toHourString(timestamp: Long):String{
        val hour = timestamp/1000/3600
        val minutes = timestamp/1000/60%60
        val second = timestamp/1000%60
        val hourStr = if(hour<10)"0$hour" else "$hour"
        val minuteStr = if(minutes<10)"0$minutes" else "$minutes"
        val secondStr = if(second<10)"0$second" else "$second"
        return "${hourStr}:${minuteStr}:${secondStr}"

    }
}