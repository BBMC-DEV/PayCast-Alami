package kr.co.bbmc.paycastdid.network

import kr.co.bbmc.paycastdid.network.model.ResDidData
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    // 서버 싱크
    @GET("/info/orderList")
    suspend fun getCookingList(
        @Query("storeId") storeId: Int,
        @Query("deviceId") deviceId: String,
    ): ResDidData
}