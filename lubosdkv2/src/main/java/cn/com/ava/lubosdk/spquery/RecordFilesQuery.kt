package cn.com.ava.lubosdk.spquery

import cn.com.ava.lubosdk.ISPQuery
import cn.com.ava.lubosdk.LuBoSDK
import cn.com.ava.lubosdk.entity.QueryResult
import cn.com.ava.lubosdk.entity.RecordFilesInfo
import cn.com.ava.lubosdk.manager.LoginManager
import cn.com.ava.lubosdk.util.URLHexEncodeDecodeUtil

class RecordFilesQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)?
) : ISPQuery<RecordFilesInfo> {


    override val name: String
        get() = "RecordFiles"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return java.util.LinkedHashMap<String,String>().apply{
            this["key"] = LoginManager.getLogin()?.key?:""
        }
    }

    override fun build(response: String): RecordFilesInfo {
        val split = response.split("|")
        val info = RecordFilesInfo()
        if (split[0] == "0") return info

        info.files = arrayListOf()
        for(i in 0 until 8){
            val file = RecordFilesInfo.RecordFile()
            val split1 = split[i].split("-").toTypedArray()
            if (split1.size == 1) continue
            file.downloadUrl = "http://${LuBoSDK.getIp()}:${LuBoSDK.getPort()}/cgi-bin/plat.cgi?action=12&key=${LoginManager.getLogin()?.key?:""}&filename=${split1[1]}"
            file.rtspUrl ="rtsp://${LuBoSDK.getIp()}:554/playback/${split1[1]}"
            val fileSize = split1[0].toLongOrNull()
            file.rawFileSize = fileSize?:0
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
            val analyseTime:(String)->String =lit@{time->
                if (time.length == 1){
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

            val fillZero:(Int,Int)->String=lit@{digit,size->
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
}