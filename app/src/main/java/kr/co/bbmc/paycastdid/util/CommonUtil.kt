@file:Suppress("DEPRECATION")

package kr.co.bbmc.paycastdid.util

import android.os.Handler
import android.os.Looper

fun delayRun(r: Runnable, delayTime: Long = 2000L) =
    Handler(Looper.getMainLooper()).postDelayed(r, delayTime)