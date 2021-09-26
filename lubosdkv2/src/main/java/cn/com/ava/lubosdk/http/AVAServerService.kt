package cn.com.ava.lubosdk.http

import cn.com.ava.lubosdk.entity.MeetingUser
import cn.com.ava.lubosdk.entity.Pager
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AVAServerService {


    @GET("/cgi-bin/plat.cgi")
    fun httpCommand(@QueryMap queryMap: Map<String, String>): Call<ResponseBody>


    @GET("/cgi-bin/plat.cgi")
    fun httpCommandFlow(@QueryMap queryMap: Map<String, String>): Call<ResponseBody>


    @GET("/cgi-bin/plat.cgi")
    fun httpCommandWithEncode(@QueryMap(encoded = true) queryMap: Map<String, String>): Call<ResponseBody>

    @POST("/cgi-bin/aupload3.cgi")
    fun uploadFile(@Body file: RequestBody): Call<ResponseBody>

    @POST("/cgi-bin/getuserbook.cgi")
    fun getRemoteAddress(@QueryMap params: Map<String, String>): Call<Pager<MeetingUser>> //获取远程通讯录

    @GET("cgi-bin/getFilesInfo.cgi")
    fun getFileInfoCommand(@QueryMap queryMap: Map<String, String>): Call<ResponseBody>


    @POST("/cgi-bin/uploadpointfile.cgi")
    fun uploadDot(@Body file: RequestBody): Call<ResponseBody>

    @GET("/cgi-bin/getProgramsInfo.cgi")
    fun getFilesInfo(): Call<ResponseBody>

    @GET("/cgi-bin/getPtFilesInfo.cgi")
    fun getPtFilesInfo(@Query("program") program: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("cgi-bin/delFile.cgi")
    fun deleteVideoResource(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("cgi-bin/plat.cgi")
    fun uploadVideoResource(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("cgi-bin/plat.cgi")
    fun deleteRecordFile(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @GET("cgi-bin/plat.cgi")
    fun getFtpUploadStatus(@QueryMap queryMap: Map<String, String>): Call<ResponseBody>

    @GET("cgi-bin/plat.cgi")
    fun getFtpUploadList(@QueryMap queryMap: Map<String, String>): Call<ResponseBody>

    @GET("cgi-bin/plat.cgi")
    fun getRecordFileList(@QueryMap queryMap: Map<String, String>): Call<ResponseBody>
}