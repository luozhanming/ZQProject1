package cn.com.ava.zqproject.common

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import androidx.core.database.getStringOrNull
import cn.com.ava.zqproject.R
import com.blankj.utilcode.util.Utils
import java.io.File

/**
 * 下载管理器
 * */
object MyDownloadManager {

    private var mDownloadManager: DownloadManager? = null


    fun startDownload(url: String, dstPath: String, fileName: String):Long {
        val uri = Uri.parse(url)

        mDownloadManager = getDownloadManager()
//        val query = DownloadManager.Query()
//        query.setFilterByStatus(DownloadManager.STATUS_RUNNING or DownloadManager.STATUS_PAUSED or DownloadManager.STATUS_PENDING or DownloadManager.STATUS_SUCCESSFUL)
//        val cursor = mDownloadManager?.query(query)
//        while (cursor?.moveToNext() == true) {
//            val urlValue = cursor.getStringOrNull(cursor.getColumnIndex(DownloadManager.COLUMN_URI))
//            if (url == urlValue) {   //已下载中
//                cursor.close()
//                return -1
//            }
//        }
     //   cursor?.close()

        val request = DownloadManager.Request(uri)
        request.setTitle(Utils.getApp().getString(R.string.software_upgrade))
        val dstFile = File("${dstPath}/${fileName}")
        //删除前一个，避免重复下载
        if(dstFile.exists()){
            dstFile.delete()
        }
        request.setDestinationUri(Uri.fromFile(dstFile))
        val enqueue = mDownloadManager?.enqueue(request) ?: 0L
        return enqueue


    }


    fun stopDownload(id:Long) {
        mDownloadManager = getDownloadManager()
        mDownloadManager?.remove(id)
    }


    private fun getDownloadManager():DownloadManager{
        return mDownloadManager ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utils.getApp().getSystemService(DownloadManager::class.java)
        } else {
            Utils.getApp().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        }
    }







}