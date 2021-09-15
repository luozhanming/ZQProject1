package cn.com.ava.lubosdk.manager

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
import java.util.*
import kotlin.collections.LinkedHashMap
import io.reactivex.Observable

object VideoResourceManager {

    /*
    * 删除视频资源
    * */
    fun deleteRecordFile(data: RecordFilesInfo.RecordFile): Observable<Boolean> {
        val params: MutableMap<String, String> = LinkedHashMap()
        params["key"] = LoginManager.getLogin()?.key ?: ""
        params["fname"] = data.rawFileName
        params["start_time"] = data.recordRawBeginTime
        logd("删除视频的params: ${params.toString()}")
        return Observable.create { e ->
            val call = AVAHttpEngine.getHttpService().deleteVideoResource(params)
            val response = call.execute()
            logd("删除视频的response：${response}")
            val bodyStr = response.body()?.string()
            logd("删除视频的body： $bodyStr")
            if (bodyStr != null) {
                if (bodyStr.contains("record deleteFile") && bodyStr.contains("filename") && bodyStr.contains("=")) {
                    e.onNext(true)
                } else {
                    e.onNext(false)
                }
            } else {
                e.onNext(false)
            }
        }
    }

    /*
    * 删除视频资源
    * */
    fun deleteRecordFile2(data: RecordFilesInfo.RecordFile): Observable<Boolean> {
        val params: MutableMap<String, String> = LinkedHashMap()
        params["action"] = "9"
        params["user"] = LoginManager.getLogin()?.username ?: ""
        params["pswd"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "")
        params["command"] = "1"
        val tempData = "record_deleteFile_filename=${data.rawFileName}"
        params["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(tempData)
        return Observable.create { e ->
            val call = AVAHttpEngine.getHttpService().deleteRecordFile(params)
            val response = call.execute()
            logd("删除视频的response：${response}")
            val bodyStr = response.body()?.string()
            logd("删除视频的body： $bodyStr")
            if (bodyStr != null) {
                e.onNext(true)
            } else {
                e.onNext(false)
            }
        }
    }

    /*
    * 上传视频资源
    * */
    fun uploadRecordFile(data: RecordFilesInfo.RecordFile): Observable<Boolean> {
        val params: MutableMap<String, String> = LinkedHashMap()
        params["key"] = LoginManager.getLogin()?.key ?: ""
        params["action"] = "1"
        params["cmd"] = "130"
        params["name"] = data.rawFileName
        return Observable.create { e ->
            val call = AVAHttpEngine.getHttpService().uploadVideoResource(params)
            val response = call.execute()
            logd("上传视频的response：${response}")
            val bodyStr = response.body()?.string()
            logd("上传视频的body： $bodyStr")
            if (bodyStr != null) {
                e.onNext(true)
            } else {
                e.onNext(false)
            }
        }
    }
}