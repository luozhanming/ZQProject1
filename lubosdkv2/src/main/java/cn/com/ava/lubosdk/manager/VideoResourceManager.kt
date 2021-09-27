package cn.com.ava.lubosdk.manager

import cn.com.ava.common.util.logd
import cn.com.ava.lubosdk.AVAHttpEngine
import cn.com.ava.lubosdk.LuBoSDK
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.entity.TransmissionProgressEntity
import cn.com.ava.lubosdk.util.EncryptUtil
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil
import java.util.*
import kotlin.collections.LinkedHashMap
import io.reactivex.Observable
import java.lang.Exception
import kotlin.collections.HashMap

object VideoResourceManager {

    /**上传状态-排队中*/
    const val UPLOAD_STATE_WAITING = "waiting"
    /**上传状态-上传中*/
    const val UPLOAD_STATE_UPLOADING = "uploading"
    /**上传状态-已完成*/
    const val UPLOAD_STATE_DONE = "done"
    /**上传状态-未知*/
    const val UPLOAD_STATE_UNKNOWN = "unknown"
    /**上传状态-出错（如果文件上传已完成，系统中仅保留2分钟该文件的状态，超过2分钟后将查询不到任何信息）*/
    const val UPLOAD_STATE_ERROR = "error"

    /*
    * 获取视频资源列表
    * */
    fun getRecordFileList(): Observable<List<RecordFilesInfo.RecordFile>> {
        val params: MutableMap<String, String> = LinkedHashMap()
        params["action"] = "9"
        params["user"] = LoginManager.getLogin()?.username ?: ""
        params["pswd"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "").lowercase()
        params["command"] = "2"
        logd("pswd = ${LoginManager.getLogin()?.password}")

        val tempData = "getRecordFileList"
        logd("getRecordFileList data = $tempData")
        params["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(tempData, "GBK")
        logd("获取文件列表的params: ${params.toString()}")
        return Observable.create { e ->
            val call = AVAHttpEngine.getHttpService().getRecordFileList(params)
            val response = call.execute()
            val bodyStr = response.body()?.string()
            logd("获取文件列表的body： $bodyStr")
            if (bodyStr != null) {
                if (bodyStr.contains("|")) {
                    val info = handleRecordFileResponse(bodyStr)
                    e.onNext(info.files)
                } else {
                    e.onNext(arrayListOf())
                }
            } else {
                e.onNext(arrayListOf())
            }
        }
    }

    fun handleRecordFileResponse(response: String): RecordFilesInfo {

        val split = response.split("|")
        val info = RecordFilesInfo()
        if (split[0] == "0" || split[0] == "-1") return info

        info.files = arrayListOf()
        for (i in 1 until split.size - 1) {
            val file = RecordFilesInfo.RecordFile()
            val split1 = split[i].split("-").toTypedArray()
            if (split1.size == 1) continue
            file.downloadUrl =
                "http://${LuBoSDK.getIp()}:${LuBoSDK.getPort()}/cgi-bin/plat.cgi?action=12&key=${LoginManager.getLogin()?.key ?: ""}&filename=${split1[1]}"
            file.rtspUrl = "rtsp://${LuBoSDK.getIp()}:554/playback/${split1[1]}"
            val fileSize = split1[0].toLongOrNull()
            file.rawFileSize = fileSize ?: 0
            var tempFileSize = file.rawFileSize.toDouble()
            var fileSizeStr = ""
            if (file.rawFileSize < 1024) {
                fileSizeStr = tempFileSize.toString() + "B"
            } else if (file.rawFileSize < 1024 * 1024) {
                tempFileSize /= 1024
                fileSizeStr = String.format("%.2fKB", tempFileSize)
            } else if (file.rawFileSize < 1024 * 1024 * 1024) {
                tempFileSize = tempFileSize / 1024 / 1024
                fileSizeStr = String.format("%.2fMB", tempFileSize)
            } else {
                tempFileSize = tempFileSize / 1024 / 1024 / 1024
                fileSizeStr = String.format("%.2fGB", tempFileSize)
            }
            file.fileSize = fileSizeStr
            file.rawFileName = split1[1]
            val name = URLHexEncodeDecodeUtil.hexToStringUrlDecode(
                split1[1], "GBK"
            ).split("-").toTypedArray()
            //解析流序号
            val stream = name[1]
            file.streamIndex = stream
            //解析录课名称
            val className = name[2]
            val split2 = className.split("_").toTypedArray()
            file.classTheme = String.format("%s_%s", split2[0], stream)
            file.teacher = split2[1]
            //解析视频信息
            val videoInfo = name[3]
            val split3 = videoInfo.split("_").toTypedArray()
            file.videoSize = split3[0]
            file.fps = split3[1]
            file.gop = split3[2]
            file.videoBps = split3[3]
            file.streamMode = split3[4]
            file.videoCodecMode = split3[5]
            //解析音频信息
            val audioInfo = name[4]
            val split4 = audioInfo.split("_").toTypedArray()
            file.audioBps = split4[0]
            file.sampleBit = split4[1]
            file.channelCount = split4[2]
            file.sampleRate = split4[3]
            file.audioCodecMode = split4[4]
            //解析录制时长信息
            //解析录制时长信息
            val time = name[5].replace(".mp4", "")
            val split5 = time.split("_").toTypedArray()
            val begin = split5[0]
            file.recordRawBeginTime = begin
            val analyseTime: (String) -> String = lit@{ time ->
                if (time.length == 1) {
                    return@lit "0000/00/00 00:00:00"
                }
                val buffer = StringBuffer()
                buffer.append(time.subSequence(0, 4)) //年

                buffer.append("/")
                buffer.append(time.subSequence(4, 6)) //月

                buffer.append("/")
                buffer.append(time.subSequence(6, 8)) //日

                buffer.append(" ")
                buffer.append(time.subSequence(8, 10)) //时

                buffer.append(":")
                buffer.append(time.subSequence(10, 12)) //分

                buffer.append(":")
                buffer.append(time.subSequence(12, 14)) //秒

                return@lit buffer.toString()
            }

            val fillZero: (Int, Int) -> String = lit@{ digit, size ->
                val s = digit.toString()
                val length = s.length
                return@lit if (size > length) {
                    val fillCount = size - length
                    val buffer = StringBuffer()
                    for (i in 0 until fillCount) {
                        buffer.append("0")
                    }
                    buffer.append(s)
                    buffer.toString()
                } else {
                    s
                }
            }

            val beginTime: String = analyseTime(begin)
            file.recordBeginTime = beginTime
            val end = split5[1]
            file.recordRawEndTime = end
            val endTime: String = analyseTime(end)
            file.recordEndTime = endTime
            val duration = split5[2].toInt()
            val hour = duration / 60 / 60
            val minute = duration / 60 % 60
            val second = duration % 60 % 60
            file.duration = String.format(
                "%s:%s:%s", fillZero(hour, 2),
                fillZero(minute, 2), fillZero(second, 2)
            )
            file.rawDuration = split5[2]
            file.downloadFileName =
                java.lang.String.format(
                    "%s_%s-%s.mp4",
                    file.classTheme,
                    file.teacher,
                    file.recordRawBeginTime
                )
            info.files.add(file)
        }
        return info

    }

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
        logd("待删除的视频 = ${data.toString()}")
        val params: MutableMap<String, String> = LinkedHashMap()
        params["action"] = "9"
        params["user"] = LoginManager.getLogin()?.username ?: ""
        params["pswd"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "").lowercase()
        params["command"] = "1"
        logd("pswd = ${LoginManager.getLogin()?.password}")

        val tempData = "record_deleteFile_filename=${data.rawFileName}"
        logd("data = $tempData")
        params["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(tempData, "GBK")
        logd("删除视频的params: ${params.toString()}")
        return Observable.create { e ->
            val call = AVAHttpEngine.getHttpService().deleteRecordFile(params)
            val response = call.execute()
            logd("删除视频的response：${response}")
            val bodyStr = response.body()?.string()
            logd("删除视频的body： $bodyStr")
            if (bodyStr != null) {
                if (bodyStr.contains("record_deleteFile_ret=ok")) {
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


    /*
    * 上传视频资源
    * */
    fun uploadRecordFile2(ftpInfo: HashMap<String, Any>, data: RecordFilesInfo.RecordFile, dstFile: String): Observable<Boolean> {
        logd("待上传的视频 = ${data.toString()}")

        val params: MutableMap<String, String> = LinkedHashMap()
        params["action"] = "9"
        params["user"] = LoginManager.getLogin()?.username ?: ""
        params["pswd"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "").lowercase()
        params["command"] = "1"
        val username = ftpInfo["ftpAcct"]
        val password = ftpInfo["ftpPsw"]
        val targetPath = ""
        val serverIp = ftpInfo["ftpUrl"]
        val serverPort = ftpInfo["ftpPort"]
        val filename = data.rawFileName
        val encode_dstFile = URLHexEncodeDecodeUtil.stringToHexEncode(dstFile, "GBK")
        logd("encode_dstFile = $encode_dstFile")
        val tempData = "ftp_uploadFileByName_username=$username,password=$password,targetPath=$targetPath,serverIp=$serverIp," +
                "serverPort=$serverPort,filename=$filename,dstFile=$encode_dstFile"
        logd("data = $tempData")
        params["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(tempData, "GBK")
        logd("上传视频的params: ${params.toString()}")
        return Observable.create { e ->
            val call = AVAHttpEngine.getHttpService().uploadVideoResource(params)
            val response = call.execute()
            logd("上传视频的response：${response}")
            val bodyStr = response.body()?.string()
            logd("上传视频的body： $bodyStr")
            if (bodyStr != null) {
                if (bodyStr.contains("ftp_uploadFileByName_ret=ok")) {
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
    * 查询上传进度
    * */
    fun checkUploadProgress(fileName: String): Observable<TransmissionProgressEntity> {

        val params: MutableMap<String, String> = LinkedHashMap()
        params["action"] = "9"
        params["user"] = LoginManager.getLogin()?.username ?: ""
        params["pswd"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "").lowercase()
        params["command"] = "1"
        val tempData = "getParam_getFtpUploadStatus_recId=,streamId=,filename=$fileName"
        logd("上传状态data = $tempData")
        params["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(tempData, "GBK")
        logd("上传状态的params: ${params.toString()}")
        return Observable.create { e ->
            val call = AVAHttpEngine.getHttpService().getFtpUploadStatus(params)
            val response = call.execute()
//            logd("上传状态的response：${response}")
            val bodyStr = response.body()?.string()
            logd("上传状态的body： $bodyStr")
            if (bodyStr != null) {
                if (!bodyStr.contains("getParam_getFtpUploadStatus_ret=error")) {
                    var progress = TransmissionProgressEntity(2,"0%",4)

//                    val result: MutableMap<String, Any> = mutableMapOf()
//                    result.put("result", true)
                    val list = bodyStr.split("&")
                    try {
                        list.forEachIndexed { index, value ->
                            if (index == 2) {
                                if (value.contains("=")) {
                                    val tempList = value.split("=")
//                                result.put("state", tempList[1])
                                    val state = tempList[1]
                                    when (state) {
                                        "waiting" -> progress.state = 0
                                        "uploading" -> progress.state = 2
                                        "done" -> progress.state = 1
                                        "unknown" -> progress.state = 3
                                        "error" -> progress.state = 3
                                        else -> progress.state = 0
                                    }
                                }
                            } else if (index == 3) {
                                if (value.contains("=")) {
                                    val tempList = value.split("=")
//                                result.put("process", tempList[1])
                                    progress.progress = tempList[1]
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    e.onNext(progress)
                } else {
                    e.onNext(TransmissionProgressEntity(2,"0%",3))
                }
            } else {
                e.onNext(TransmissionProgressEntity(2,"0%",3))
            }
        }

    }

    /*
    * 获取上传列表
    * */
    fun getUploadList(): Observable<Boolean> {

        val params: MutableMap<String, String> = LinkedHashMap()
        params["action"] = "9"
        params["user"] = LoginManager.getLogin()?.username ?: ""
        params["pswd"] = EncryptUtil.encryptMD5ToString(LoginManager.getLogin()?.password ?: "").lowercase()
        params["command"] = "1"
        val tempData = "getParam_getFtpUploadList"
        logd("上传列表data = $tempData")
        params["data"] = URLHexEncodeDecodeUtil.stringToHexEncode(tempData, "GBK")
        logd("上传列表的params: ${params.toString()}")
        return Observable.create { e ->
            val call = AVAHttpEngine.getHttpService().getFtpUploadList(params)
            val response = call.execute()
            logd("上传列表的response：${response}")
            val bodyStr = response.body()?.string()
            logd("上传列表的body： $bodyStr")
            if (bodyStr != null) {
//                if (!bodyStr.contains("getParam_getFtpUploadStatus_ret=error")) {
//
//                    val result: MutableMap<String, Any> = mutableMapOf()
//                    result.put("result", true)
//                    val list = bodyStr.split("&")
//                    list.forEachIndexed { index, value ->
//                        if (index == 2) {
//                            if (value.contains("=")) {
//                                val tempList = value.split("=")
//                                result.put("state", tempList[1])
//                            }
//                        } else if (index == 3) {
//                            if (value.contains("=")) {
//                                val tempList = value.split("=")
//                                result.put("process", tempList[1])
//                            }
//                        }
//                    }
//                    e.onNext(result.toMap())
//                } else {
//                    e.onNext(mapOf("result" to false))
//                }
                e.onNext(false)
            } else {
                e.onNext(false)
            }
        }

    }
}