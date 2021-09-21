package ir.am3n.needtool

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment


fun Context.sh(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences {
    return getSharedPreferences(name, mode)
}
fun Fragment.sh(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences? {
    return context?.getSharedPreferences(name, mode)
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

