package kr.co.bbmc.paycastdid.presentation.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.core.view.WindowCompat
import com.orhanobut.logger.Logger
import kr.co.bbmc.paycast.ui.component.theme.AdNetTheme

class CustomMainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }
}