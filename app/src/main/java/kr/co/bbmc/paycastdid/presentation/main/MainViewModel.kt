package kr.co.bbmc.paycastdid.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kr.co.bbmc.paycastdid.BaseViewModel
import kr.co.bbmc.paycastdid.DidExternalVarApp.Companion.APP
import kr.co.bbmc.paycastdid.network.model.CookingData
import kr.co.bbmc.paycastdid.util.baseExceptionHandler
import kr.co.bbmc.selforderutil.FileUtils
import java.io.File

@FlowPreview
class MainViewModel: BaseViewModel() {

    private val _didInfo = MutableStateFlow<List<CookingData>?>(null)
    val didInfo = _didInfo.asStateFlow()

    var _dpInfo = MutableLiveData<String>()
    val dpInfo = _dpInfo
    fun setDp(dp: String) = _dpInfo.postValue(dp)

    fun getDidInfo(): Job = viewModelScope.launch(Dispatchers.IO + baseExceptionHandler() {
        Logger.e("Get orderNum error - $it")
        sendToast("Network Error: $it")
    }) {
        APP.repository.getDidInfo()
            .retry(2)
            .collectLatest {
                Logger.w("DidData : ${it.data}")
                _didInfo.value = it.data
            }
    }

    fun checkDirectory() {
        var defaultDir = File(FileUtils.BBMC_DEFAULT_DIR)
        if (!defaultDir.exists()) defaultDir.mkdir()
        defaultDir = File(FileUtils.BBMC_PAYCAST_DIRECTORY)
        if (!defaultDir.exists()) defaultDir.mkdir()
        defaultDir = File(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY)
        if (!defaultDir.exists()) defaultDir.mkdir()
        defaultDir = File(FileUtils.BBMC_PAYCAST_BG_DIRECTORY)
        if (!defaultDir.exists()) defaultDir.mkdir()
    }
}