package kr.co.bbmc.paycastdid.util

import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineExceptionHandler

fun baseExceptionHandler(callback: ((String) -> Unit)? = null): CoroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
    try { callback?.invoke(e.message ?: "Uncaught Error") } catch (e: Exception) { Logger.e("Error: ${e.message}") }
}