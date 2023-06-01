package kr.co.bbmc.paycastdid.network

@Suppress("unused")
object RemoteConstant {
    const val TIMEOUT_CONNECT = 30L
    const val TIMEOUT_READ = 30L
    const val TIMEOUT_WRITE = 30L

    const val LOGGING_TYPE_NONE = 0
    const val LOGGING_TYPE_GENERAL = 1

    const val HEADER_AUTH = "Authorization"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val APPLICATION_JSON = "application/json"

    //const val BASE_URL =  "192.168.0.25"
    const val BASE_URL =  "http://s.paycast.co.kr"
    const val TEST_URL =  "https://m.paycast.co.kr"

    var REQUEST_BASE_URL = ""
}