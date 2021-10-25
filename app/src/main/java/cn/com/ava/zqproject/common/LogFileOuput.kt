package cn.com.ava.zqproject.common

import cn.com.ava.zqproject.AppConfig
import java.io.RandomAccessFile
import java.lang.Exception

object LogFileOuput {

    lateinit var file :RandomAccessFile

    init {

//        try {
//            file = RandomAccessFile("${AppConfig.PATH_FILE_LOG}/${System.currentTimeMillis()}.txt","rw")
//            file.seek(file.length())
//        }catch (e:Exception){
//
//        }

    }


    fun write(content:String){
     //   file.writeBytes(content)
    }







    fun release(){
  //      file.close()
    }


}