package cn.com.ava.zqproject.net

import cn.com.ava.zqproject.net.PlatformApiManager.PATH_GET_INTERFACE
import cn.com.ava.zqproject.vo.*
import io.reactivex.Observable
import retrofit2.http.*
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
    @FormUrlEncoded
    @POST("/{path}")
    fun refreshToken(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_REFRESH_TOKEN
        ), @Field("token") token: String = ""
    ): Observable<PlatformResponse<String>>


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


    @GET("/{path}")
    fun requestUpgrade(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_APP_LATEST_VERSION
        ),
        @Query("platformIdentify") platformIdentify: Int = 1,/*平台标识:0-未知1-Android2-ios*/
        @Query("softwareIdentity") softwareIdentity: Int = 1 /**/
    ): Observable<PlatformResponse<AppUpgrade>>


    /**
     * 退出登录
     * */
    @FormUrlEncoded
    @POST("/{path}")
    fun logout(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_LOGOUT
        ),
        @Field("token") token: String = "",/*平台标识:0-未知1-Android2-ios*/

    ): Observable<PlatformResponse<Any>>


    /**
     * 发送心跳
     * @param rsAcct 互动账号
     * */
    @FormUrlEncoded
    @POST("/{path}")
    fun heartBeat(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_HEART_BEAT
        ),
        @Field("rsAcct") rsAcct: String = ""
    ): Observable<PlatformResponse<Any>>

    /**
     * 保存FTP中的视频文件
     * */
    @FormUrlEncoded
    @POST("/{path}")
    fun saveVideoFromFTP(
        @Path("path", encoded = true) path: String? = PlatformApiManager.getApiPath(
            PlatformApiManager.PATH_SAVE_FTP_VIDEO
        ),
        @Field("fileName") fileName: String = "",
        @Field("title") title: String = "",
        @Field("period") period: Int = 0,
        @Field("uuid") uuid: String = "",
    ): Observable<PlatformResponse<Any>>
}