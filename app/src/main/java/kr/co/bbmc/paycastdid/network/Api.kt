package kr.co.bbmc.paycastdid.network

import kr.co.bbmc.paycastdid.network.model.ResDidData
import kr.co.bbmc.paycastdid.network.model.ResRegisterToken
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {
    // 서버 싱크
    @GET("/info/orderList")
    suspend fun getCookingList(
        @Query("storeId") storeId: Int,
        @Query("deviceId") deviceId: String,
    ): ResDidData

    @POST("/dsg/agent/token")
    suspend fun registerFCMToken(
        @Query("deviceId") deviceId: String,
        @Query("token") token: String
    ): ResRegisterToken

}