package ir.am3n.needtool

import androidx.annotation.ColorInt

val Int.toRgb: IntArray get() {
    val rgb = IntArray(3)
    rgb[0] = (this shr 16 and 0xFF)//.toByte()
    rgb[1] = (this shr  8 and 0xFF)//.toByte()
    rgb[2] = (this        and 0xFF)//.toByte()
    return rgb
}