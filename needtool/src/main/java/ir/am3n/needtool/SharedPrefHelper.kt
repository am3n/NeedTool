package ir.am3n.needtool

import android.content.Context
import android.content.SharedPreferences


fun Context.sh(name: String): SharedPreferences {
    return sh(name, Context.MODE_PRIVATE)
}
fun Context.sh(name: String, mode: Int): SharedPreferences {
    return getSharedPreferences(name, mode)
}
fun SharedPreferences.str(name: String, def: String = ""): String {
    return getString(name, def) ?:def
}
fun SharedPreferences.int(name: String, def: Int = 0): Int {
    return getInt(name, def)
}
fun SharedPreferences.bool(name: String, def: Boolean = false): Boolean {
    return getBoolean(name, def)
}

