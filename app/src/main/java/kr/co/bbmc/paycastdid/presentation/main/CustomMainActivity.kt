package kr.co.bbmc.paycastdid.presentation.main

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.orhanobut.logger.Logger
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.co.bbmc.paycast.ui.component.theme.AdNetTheme
import kr.co.bbmc.paycastdid.R
import kr.co.bbmc.paycastdid.deviceId
import kr.co.bbmc.paycastdid.firebaseMsg
import kr.co.bbmc.paycastdid.storeId
import kr.co.bbmc.paycastdid.util.checkDeviceDpi
import kr.co.bbmc.paycastdid.util.delayRun
import kr.co.bbmc.paycastdid.util.parsePlayerOptionXMLV2
import kr.co.bbmc.paycastdid.util.repeatOnState
import kr.co.bbmc.selforderutil.FileUtils

@FlowPreview
class CustomMainActivity: ComponentActivity() {

    private lateinit var vm: MainViewModel
    private lateinit var soundPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[MainViewModel::class.java]
        soundPlayer = MediaPlayer.create(this, R.raw.alarmsound)

        Logger.w("MainActivity : onCreate!!")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            run {
                AdNetTheme {
                    Surface {
                        Modifier.padding(vertical = 20.dp)
                        MainScreen(vm)
                    }
                }
            }
        }
        requestPermission()
        observerData()
    }
    private fun initData() {
        getFcmToken()
        //TODO: xml parser로 스토어 정보와 deviceId 정보 가져오기
        vm.checkDirectory()
        vm.setDp(checkDeviceDpi())
        loadDidInfoFromXmlFiles()
    }

    private fun getFcmToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener<InstanceIdResult> { task ->
                if (!task.isSuccessful) {
                    Logger.e("getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result!!.token
                // Log and toast
                Logger.e("get Togken : $token")
                //TODO: firebase token send to server!!!
                vm.registerToken(token)
                //sendRegistrationToServer(token)
//                mDidExterVarApp.token = token
//                if (MainActivity.fcmTokenTimer == null) {
//                    if (LOG) Log.e(MainActivity.TAG, "fcmTokenTimerTask() SET")
//                    MainActivity.fcmTokenTimer = Timer("fcmTokenTimer")
//                    MainActivity.fcmTokenTimer.schedule(fcmTokenTimerTask(), 1000)
//                }
            })

    }

    private fun requestPermission() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    Logger.w("Init START!! - permission granted!!!")
                    initData()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    vm.sendToast("파일 Access 권한이 필요합니다.")
                    Logger.e("runtime permission denied!!")
                    CoroutineScope(Dispatchers.IO).launch { vm.showDialog(true) }
                    //delayRun({ exitApp() }, 1000L)
                }
            })
            .setDeniedMessage("파일 Access 권한이 필요합니다.")
            .setPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun loadDidInfoFromXmlFiles() {
        try {
            val testDir = "/sdcard/BBMC/PAYCAST/DATA/" + FileUtils.getFilename(FileUtils.PayCastDid)
            val destDir = FileUtils.BBMC_PAYCAST_DATA_DIRECTORY + FileUtils.getFilename(FileUtils.PayCastDid)
            Logger.w("primaryExternalStorageSD : $destDir")
            when(parsePlayerOptionXMLV2(testDir)) {
                true -> {
                    Logger.w("Success xml parse: StoreId - $storeId & DeviceId - $deviceId")
                    vm.getDidInfo()
                    vm.repeatTask()
                }
                else -> { Logger.e("Parse Err")
                    vm.sendToast("xml정보를 읽어올수 없습니다.")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observerData() {

        firebaseMsg.observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Logger.w("Firebase msg updated! - $it")
                delayRun({ vm.getDidInfo() }, 700L)
                vm.sendToast("Firebase msg updated! - $it")
            }, {
                vm.sendToast(it.message ?: "Error")
            })
            .addTo(vm.compositeDisposable)

        vm.toast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        repeatOnState(Lifecycle.State.RESUMED) {
            vm.didInfo.collectLatest {
                Logger.e("Cooking info changes : $it")
                it?.let {
                    if(::soundPlayer.isInitialized) soundPlayer.start()
                }
            }
        }
    }
}