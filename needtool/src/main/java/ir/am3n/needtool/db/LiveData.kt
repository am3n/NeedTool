package ir.am3n.needtool.db

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner?, observer: Observer<T?>) {
    lifecycleOwner?.let {
        observe(it, object : Observer<T?> {
            override fun onChanged(value: T?) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }
}