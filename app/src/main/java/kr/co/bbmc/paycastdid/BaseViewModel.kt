package kr.co.bbmc.paycastdid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import kr.co.bbmc.paycast.ui.component.theme.ButtonType
import kr.co.bbmc.paycastdid.model.DlgInfo
import java.util.concurrent.TimeUnit

open class BaseViewModel() : ViewModel() {

    val compositeDisposable = CompositeDisposable()

    private val _toast = MutableLiveData<String>()
    val toast: LiveData<String> = _toast
    fun sendToast(msg: String) = _toast.postValue(msg)

    private val _showDlg = MutableLiveData(false)
    val showDlg = _showDlg
    fun showDialog(show: Boolean) = _showDlg.postValue(show)

    private val _dlgInfo = MutableLiveData<DlgInfo>()
    val dlgInfo = _dlgInfo
    fun setDlgInfo(dlg: DlgInfo) = _dlgInfo.postValue(dlg)
    fun showPopupDlg(title: String, contents: String, icon: Int? = null, type: ButtonType = ButtonType.Single) {
        showDialog(true)
        setDlgInfo(
            DlgInfo(
                type = type,
                contentTitle = title,
                contents = contents,
                positiveCallback = { showDialog(false) },
                iconResource = icon
            )
        )
    }
    private var countTimerObserver: Disposable? = null
    fun countTimer(baseTime: Long, callback: ()->Unit) {
        releaseTimer()
        countTimerObserver = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { baseTime - it }
            .takeWhile { it > 0L }
            .onErrorComplete()
            .subscribe({
                if (it <= 1L) {
                    callback.invoke()
                }
            }, {}).addTo(compositeDisposable)
    }
    fun releaseTimer() {
        countTimerObserver?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        countTimerObserver = null
    }

    override fun onCleared() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        super.onCleared()
    }
}