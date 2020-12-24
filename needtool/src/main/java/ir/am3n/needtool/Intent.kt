package ir.am3n.needtool

import android.content.Intent

fun Intent.bool(name: String, default: Boolean = false): Boolean {
    if (hasExtra(name))
        return getBooleanExtra(name, default)
    return default
}