package kr.co.bbmc.paycastdid.util

import android.content.BroadcastReceiver
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.*
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