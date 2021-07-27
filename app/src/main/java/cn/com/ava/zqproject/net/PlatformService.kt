package cn.com.ava.zqproject.net

import cn.com.ava.zqproject.net.PlatformApiManager.PATH_GET_INTERFACE
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.PlatformResponse
import cn.com.ava.zqproject.vo.PlatformSetting
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlatformService {


    @GET("/{path}")
    fun getInterface(
        @Path("path", encoded = true) path: String = PlatformApiManager.getApiPath(
            PATH_GET_INTERFACE
        ) ?: ""
    ): Observable<PlatformResponse<PlatformSetting>>


    /**
     * 获取通讯录
     * */
    @GET("/{path}")
    fun getContractUsers( @Path("path",encoded = true) path:String?=PlatformApiManager.getApiPath(PlatformApiManager.PATH_ADDRESS_LIST),@Query("pageIndex") pageIndex: Int = 1, @Query("pageSize") pageSize: Int = 1000,
                        ):Observable<PlatformResponse<List<ContractUser>>>





}