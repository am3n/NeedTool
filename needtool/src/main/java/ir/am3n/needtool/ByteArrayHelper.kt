package ir.am3n.needtool

fun change2by2(bytes: ByteArray): ByteArray? {
    val changed = ByteArray(bytes.size)
    var i = 0
    while (i < bytes.size) {
        val temp = bytes[i]
        bytes[i] = bytes[i + 1]
        bytes[i + 1] = temp
        i += 2
    }
    return changed
}