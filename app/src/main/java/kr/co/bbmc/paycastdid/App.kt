package kr.co.bbmc.paycastdid

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //App 디폴드 화면을 Dark 모드로 설정
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        APP = this
        //ireStoreDataSource.init(SCREEN_ID) //릴리즈 버전에서는 사용하지 않는다.
        Logger.addLogAdapter(AndroidLogAdapter())
        initPreference()
    }

    private fun initPreference() {

    }

    companion object {
        lateinit var APP: App
    }
}