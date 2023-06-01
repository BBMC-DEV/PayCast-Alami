package kr.co.bbmc.paycastdid.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.co.bbmc.paycastdid.deviceId
import kr.co.bbmc.paycastdid.storeId
import kr.co.bbmc.paycastdid.util.parsePlayerOptionXMLV2
import kr.co.bbmc.paycastdid.util.repeatOnState
import kr.co.bbmc.selforderutil.FileUtils
import java.io.File

@FlowPreview
class CustomMainActivity: AppCompatActivity() {

    private lateinit var vm: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[MainViewModel::class.java]

        Logger.w("MainActivity : onCreate!!")
        WindowCompat.setDecorFitsSystemWindows(window, false)
//        setContent {
//            run {
//                AdNetTheme {
//                    Surface {
//                        //PaymentScreen()
//                    }
//                }
//            }
//        }
        requestPermission()
        observerData()
    }
    private fun initData() {
        //TODO: xml parser로 스토어 정보와 deviceId 정보 가져오기
        vm.checkDirectory()
        loadDidInfoFromXmlFiles()
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

        vm.toast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        repeatOnState(Lifecycle.State.RESUMED) {
            vm.didInfo.collectLatest {
                Logger.e("Cooking info changes : $it")
            }
        }

    }
}