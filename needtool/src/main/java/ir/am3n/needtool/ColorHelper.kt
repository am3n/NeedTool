package ir.am3n.needtool

import android.graphics.Color

val Int.toRgb: IntArray get() {
    val rgb = IntArray(3)
    rgb[0] = (this shr 16 and 0xFF)//.toByte()
    rgb[1] = (this shr  8 and 0xFF)//.toByte()
    rgb[2] = (this        and 0xFF)//.toByte()
    return rgb
}


val Int.isTrans: Boolean get() {
    return Color.alpha(this) <= 20
}

val Int.isDark: Boolean get() {
    return 0.299 * Color.red(this) + 0.0f + 0.590 * Color.green(this) + 0.0f + 0.114 * Color.blue(this) + 0.0f < 150
}

val Int.isDarkNotTrans: Boolean get() {
    return isDark && !isTrans
}
