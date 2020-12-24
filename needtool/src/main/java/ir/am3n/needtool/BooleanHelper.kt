package ir.am3n.needtool

val Int.bool: Boolean get() = this > 0

fun String?.toBooleanOrNull(): Boolean? {
    try {
        if (this != null && this != "null" && this != "Null" && this != "NULL")
            return this.toBoolean()
    } catch (e: Exception) {
    }
    return null
}

operator fun Boolean.inc() = !this