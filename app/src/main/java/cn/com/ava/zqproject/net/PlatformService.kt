package cn.com.ava.zqproject.net

import cn.com.ava.zqproject.net.PlatformApiManager.PATH_GET_INTERFACE
import cn.com.ava.zqproject.vo.ContractGroup
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.PlatformResponse
import cn.com.ava.zqproject.vo.PlatformSetting
import io.reactivex.Observable
import retrofit2.http.*

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
    fun getContractUsers(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_ADDRESS_LIST
        ),
        @Query("pageIndex") pageIndex: Int = 1, @Query("pageSize") pageSize: Int = 1000,
    ): Observable<PlatformResponse<List<ContractUser>>>


    /**
     * 最近呼叫
     * */

    @GET("/{path}")
    fun getRecentCall(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_RECENT_CALL
        ),
        @Query("pageIndex") pageIndex: Int = 1, @Query("pageSize") pageSize: Int = 1000,
    ): Observable<PlatformResponse<List<ContractUser>>>


    /**
     * 刷新凭证
     * */
    @POST("/{path}")
    fun refreshToken(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_REFRESH_TOKEN
        ), @Field("token") token: String = ""
    )


    /**
     * 编辑通讯群组
     * */
    @FormUrlEncoded
    @POST("/{path}")
    fun editGroup(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_COMUNICATION_GROUP_EDIT
        ),
        @Field("id") id: String = "",
        @Field("name") name: String = "",
        @Field("userIdJson") userIdJson: String = ""
    ): Observable<PlatformResponse<ContractGroup>>


    /**
     * 获取通讯录群组
     * */
    @GET("/{path}")
    fun getContractGroups(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_COMUNICATION_GROUP_LIST
        )
    ): Observable<PlatformResponse<List<ContractGroup>>>


    /**
     * 创建会议
     * */
    @FormUrlEncoded
    @POST("/{path}")
    fun createMeeting(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_CREATE_MEETING
        ),
        @Field("initiatorName") initiator: String = "",
        @Field("meetingTitle") meetingTitle: String = "",
        @Field("participantUserId") userId: String = ""
    )


}