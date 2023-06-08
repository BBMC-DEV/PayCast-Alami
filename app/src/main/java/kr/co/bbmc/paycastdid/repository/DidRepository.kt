package kr.co.bbmc.paycastdid.repository

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kr.co.bbmc.paycastdid.deviceId
import kr.co.bbmc.paycastdid.network.ApiProvider
import kr.co.bbmc.paycastdid.network.model.ResDidData
import kr.co.bbmc.paycastdid.network.model.ResRegisterToken
import kr.co.bbmc.paycastdid.storeId

@FlowPreview
class DidRepository {

    private val baseApiProvider by lazy { ApiProvider.getBaseApi() }

    // did info list
    suspend fun getDidInfo(): Flow<ResDidData> = flow {
        emit(baseApiProvider.getCookingList(storeId = storeId, deviceId = deviceId))
    }.debounce(1000L)

    suspend fun registerFCMToken(token: String): Flow<ResRegisterToken> = flow {
        emit(baseApiProvider.registerFCMToken(deviceId = deviceId, token = token))
    }

}