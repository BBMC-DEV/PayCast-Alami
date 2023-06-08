package kr.co.bbmc.paycastdid.util

import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kr.co.bbmc.paycastdid.DidExternalVarApp.Companion.APP
import kr.co.bbmc.paycastdid.deviceId
import kr.co.bbmc.paycastdid.network.RemoteConstant.BASE_URL
import kr.co.bbmc.selforderutil.AuthKeyFile
import kr.co.bbmc.selforderutil.NetworkUtil
import kr.co.bbmc.selforderutil.ServerReqUrl

fun baseExceptionHandler(callback: ((String) -> Unit)? = null): CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, e ->
        try {
            callback?.invoke(e.message ?: "Uncaught Error")
        } catch (e: Exception) {
            Logger.e("Error: ${e.message}")
        }
    }

@FlowPreview
fun sendRegistrationToServer(token: String) {
    // TODO: Implement this method to send token to your app server.
    val pInfo = AuthKeyFile.getProductInfo()
    Logger.w("pInfo : $pInfo")
    //if (pInfo != null) {
    //AuthKeyFile.onSetFcmToken(token)
    val tokenSaveUrl = ServerReqUrl.getServerSaveTokenUrl(
        BASE_URL,
        80,
        APP.applicationContext
    )
    val tokenParam = deviceId//AuthKeyFile.getFcmTokenParam()
    Logger.e("tokenParam : $tokenParam")
    if (!NetworkUtil.isConnected(APP.applicationContext)) {
        Logger.e("Msg_InvalidStbStatusAlert token")
        return
    }
    val response =  // N : 디바이스 유효기한 만료, Y 성공
        NetworkUtil.HttpResponseString(tokenSaveUrl, tokenParam, APP.applicationContext, false)
    Logger.d("Token response = $response")
    //}
}

