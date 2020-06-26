package ir.am3n.needtool

import android.content.Context
import android.content.SharedPreferences


fun Context.sh(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences {
    return getSharedPreferences(name, mode)
}
fun SharedPreferences.str(name: String, def: String = ""): String {
    return getString(name, def) ?:""
}
fun SharedPreferences.bool(name: String, def: Boolean = false): Boolean {
    return getBoolean(name, def)
}

