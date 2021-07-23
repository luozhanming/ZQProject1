package cn.com.ava.zqproject.net

import cn.com.ava.zqproject.net.PlatformApiManager.PATH_GET_INTERFACE
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface PlatformService {


    @GET("{path}")
    fun getInterface(
        @Path("path") path: String = PlatformApiManager.getApiPath(PATH_GET_INTERFACE) ?: ""
    ):Observable<ResponseBody>







}