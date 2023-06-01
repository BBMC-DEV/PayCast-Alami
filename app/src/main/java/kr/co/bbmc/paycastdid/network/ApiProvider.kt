package kr.co.bbmc.paycastdid.network

import kr.co.bbmc.paycastdid.network.NetworkProvider.provideOkHttpClient
import kr.co.bbmc.paycastdid.network.NetworkProvider.provideRxRetrofitBuilder
import kr.co.bbmc.paycastdid.network.RemoteConstant.BASE_URL
import retrofit2.Retrofit

object ApiProvider {
    fun getBaseApi(url: String = BASE_URL): Api = provideBaseRetrofit(url).create(Api::class.java)
    private fun provideBaseRetrofit(baseUrl: String = BASE_URL): Retrofit {
        return provideRxRetrofitBuilder()
            .baseUrl(baseUrl)
            .client(provideOkHttpClient())
            .build()
    }
}