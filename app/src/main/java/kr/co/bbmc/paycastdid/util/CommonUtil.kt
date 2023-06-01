@file:Suppress("DEPRECATION")

package kr.co.bbmc.paycastdid.util

import android.app.Activity
import android.graphics.Point
import android.util.DisplayMetrics
import com.orhanobut.logger.Logger

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
