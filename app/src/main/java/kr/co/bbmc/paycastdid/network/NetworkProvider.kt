package kr.co.bbmc.paycastdid.network

import com.google.gson.*
import io.reactivex.rxjava3.schedulers.Schedulers
import kr.co.bbmc.paycastdid.network.RemoteConstant.APPLICATION_JSON
import kr.co.bbmc.paycastdid.network.RemoteConstant.HEADER_AUTH
import kr.co.bbmc.paycastdid.network.RemoteConstant.HEADER_CONTENT_TYPE
import kr.co.bbmc.paycastdid.network.RemoteConstant.LOGGING_TYPE_GENERAL
import kr.co.bbmc.paycastdid.network.RemoteConstant.LOGGING_TYPE_NONE
import kr.co.bbmc.paycastdid.network.RemoteConstant.TIMEOUT_CONNECT
import kr.co.bbmc.paycastdid.network.RemoteConstant.TIMEOUT_READ
import kr.co.bbmc.paycastdid.network.RemoteConstant.TIMEOUT_WRITE
import kr.co.bbmc.paycastdid.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

@Suppress("unused")
object NetworkProvider {

    private val gson = GsonBuilder().setLenient().create()

    internal fun provideRxRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    internal fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
            .run {
                addInterceptor(GeneralInterceptor())
                addInterceptor(provideLoggingInterceptor(1))
                build()
            }

    private fun provideLoggingInterceptor(@Suppress("SameParameterValue") loggingType: Int): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = when (loggingType) {
                LOGGING_TYPE_GENERAL -> if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
                LOGGING_TYPE_NONE -> HttpLoggingInterceptor.Level.BODY
                else -> throw Exception("not supported logging type")
            }
        }

    internal class AuthInterceptor(private val authToken: String) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain):
                Response = with(chain) {
            val newRequest = request().newBuilder().run {
                addHeader(HEADER_AUTH, authToken)
                build()
            }
            proceed(newRequest)
        }
    }

    internal class GeneralInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain):
                Response = with(chain) {
            val newRequest = request().newBuilder()
                .run {
                    addHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON)
                    build()
                }
            proceed(newRequest)
        }
    }
}