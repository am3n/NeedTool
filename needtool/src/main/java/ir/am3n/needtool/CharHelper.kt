package ir.am3n.needtool


val Char.intOrNull: Int? get() {
    return toString().toIntOrNull()
}

val Char.intOr0: Int get() {
    return toString().toIntOrNull() ?:0
}