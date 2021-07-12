package cn.com.ava.lubosdk.query

import cn.com.ava.lubosdk.IDownloadQuery
import cn.com.ava.lubosdk.manager.LoginManager
import com.blankj.utilcode.util.EncryptUtils
import okhttp3.ResponseBody
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.LinkedHashMap

class DownloadBgFileQuery(
        val index: String,/*页码*/
        val savePath: String,/*保存路径*/
        override var onResult: (Boolean) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null) : IDownloadQuery {


    override val name: String
        get() = "DownloadBgFile"

    override fun getQueryParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "6"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pwsd"] = EncryptUtils.encryptMD5ToString(LoginManager.getLogin()?.password).toLowerCase();

            this["command"] = "2"
            this["data"] = "WhiteBoard_getLayout_$index"
        }
    }

    override fun build(response: ResponseBody): Boolean {
        val file = File(savePath)
        if (file.exists()) file.delete()
        val dataStream = response.byteStream()
        val fos = FileOutputStream(file)
        val bos = BufferedOutputStream(fos)
        val bis = BufferedInputStream(dataStream)
        try {
            val buffer = ByteArray(1024)
            var size = bis.read(buffer)
            while (size != -1) {
                bos.write(buffer, 0, size)
                size = bis.read(buffer)
            }
        } catch (e: Exception) {
            return false
        } finally {
            bos.close()
            bis.close()
        }
        return true
    }




}