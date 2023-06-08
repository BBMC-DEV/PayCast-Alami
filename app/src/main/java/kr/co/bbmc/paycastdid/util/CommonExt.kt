package kr.co.bbmc.paycastdid.util

import android.app.Activity
import android.content.BroadcastReceiver
import android.graphics.Point
import android.util.DisplayMetrics
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.*
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun <T> SavedStateHandle.getOrDefault(
    key: String, defaultValue: T
) = this[key] ?: defaultValue

fun BroadcastReceiver.goAsync(block: suspend CoroutineScope.() -> Unit) {
    val pendingResult = goAsync()
    CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
        try {
            block()
        } finally {
            pendingResult.finish()
        }
    }
}

fun LifecycleOwner.repeatOnState(
    state: Lifecycle.State = Lifecycle.State.RESUMED,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(state, block)
    }
}

fun ActivityResultCaller.registerResultCode(action: (ActivityResult?) -> Unit) = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    action(it)
}

fun Activity.checkDeviceDpi(): String {
    var dpi = ""
    val display = this.windowManager?.defaultDisplay
    val size = Point()
    display?.getSize(size)

    val metrics = DisplayMetrics()
    this.windowManager?.defaultDisplay?.getMetrics(metrics)
    val densityDpi = kotlin.runCatching { metrics.densityDpi }.getOrDefault(-1)
    Logger.w("해상도 : ${size.x} x ${size.y}")
    Logger.w("densityDpi : $densityDpi")
    if (metrics.densityDpi<=160) { // mdpi
        dpi = "mdpi"
    } else if (metrics.densityDpi<=240) { // hdpi
        dpi = "hdpi"
    } else if (metrics.densityDpi<=320) { // xhdpi
        dpi = "xhdpi"
    } else if (metrics.densityDpi<=480) { // xxhdpi
        dpi = "xxhdpi"
    } else if (metrics.densityDpi<=640) { // xxxhdpi
        dpi = "xxxhdpi"
    }
    return "해상도: ${size.x} x ${size.y} - dpi: $densityDpi"
}