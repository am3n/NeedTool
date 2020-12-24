package ir.am3n.needtool


fun Char.toIntOrNull(): Int? {
    return toString().toIntOrNull()
}

fun Char.toIntOr0(): Int {
    return toString().toIntOrNull() ?:0
}