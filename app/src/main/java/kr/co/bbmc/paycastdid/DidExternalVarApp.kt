package kr.co.bbmc.paycastdid

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kotlinx.coroutines.FlowPreview
import kr.co.bbmc.paycastdid.model.BlinkAlarmData
import kr.co.bbmc.paycastdid.model.DidAlarmData
import kr.co.bbmc.paycastdid.model.OrderListItem
import kr.co.bbmc.paycastdid.repository.DidRepository
import kr.co.bbmc.selforderutil.CommandObject
import kr.co.bbmc.selforderutil.DidOptionEnv

@FlowPreview
class DidExternalVarApp : Application() {
    @JvmField
    var mDidStbOpt: DidOptionEnv? = null
    @JvmField
    var token = ""
    @JvmField
    var newcommandList = ArrayList<CommandObject>()
    var commandList = ArrayList<CommandObject>()
    @JvmField
    var alarmDataList: ArrayList<DidAlarmData> = ArrayList()
    @JvmField
    var blinkDataList: ArrayList<BlinkAlarmData> = ArrayList()
    @JvmField
    var completeList: ArrayList<DidAlarmData> = ArrayList()
    @JvmField
    var alarmIdList: ArrayList<String> = ArrayList()
    private val mDidOrderList: MutableList<OrderListItem>? = ArrayList()
    private var mWaitOrderCount = -1

    val repository by lazy { DidRepository() }

    override fun onCreate() {
        super.onCreate()
        APP = this
        SettingEnvPersister.initPrefs(this)
        Logger.addLogAdapter(AndroidLogAdapter())
        Logger.w("Init APP")
    }

    fun addMenuObject(item: OrderListItem) {
        mDidOrderList?.add(item)
    }

    fun removeMenuObject(item: OrderListItem) {
        mDidOrderList?.remove(item)
    }

    var waitOrderCount: Int
        get() = mWaitOrderCount
        set(count) {
            mWaitOrderCount = count
            Logger.d("DID TEST setWaitOrderCount()=$mWaitOrderCount")
        }
    val menuObject: List<OrderListItem>?
        get() = mDidOrderList

    companion object {
        lateinit var APP: DidExternalVarApp
    }
}