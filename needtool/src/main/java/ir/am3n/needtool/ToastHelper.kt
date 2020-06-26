package ir.am3n.needtool

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes


fun Context.toast(@StringRes resource: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resource, duration).show()
}
fun Context.ltoast(@StringRes resource: Int) {
    Toast.makeText(this, resource, Toast.LENGTH_LONG).show()
}

fun Context.toast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text.orEmpty(), duration).show()
}
fun Context.ltoast(text: String?) {
    Toast.makeText(this, text.orEmpty(), Toast.LENGTH_LONG).show()
}
