package ir.am3n.needtool

import java.math.BigDecimal
import java.math.BigInteger

fun String.isNumeric() = toLongOrNull() != null

fun String.isAlphabet() = matches(Regex("[a-zA-Z]+"))

fun String.isAlphabetAndNumber() = matches(Regex("[a-zA-Z0-9]+")) && !isNumeric() && !isAlphabet()

fun String.isPhoneNumber() = isNumeric() &&
        ((startsWith("09") && length == 11) ||
                (startsWith("989") && length == 12) ||
                (startsWith("+989") && length == 13))

fun String.replaceLast(delimiter: String, replace: String): String {
    val index = lastIndexOf(delimiter)
    return this.replaceRange(index, index+delimiter.length, replace)
}


inline fun <reified T: Any> String.removeNoneNumeric(): T {
    return when (T::class) {
        Short::class -> {
            return (replace(Regex("[^\\d]"), "").toShortOrNull() ?: 0) as T
        }
        Int::class -> {
            return (replace(Regex("[^\\d]"), "").toIntOrNull() ?: 0) as T
        }
        Long::class -> {
            return (replace(Regex("[^\\d]"), "").toLongOrNull() ?: 0) as T
        }
        BigInteger::class -> {
            return (replace(Regex("[^\\d]"), "").toBigIntegerOrNull() ?: 0) as T
        }
        BigDecimal::class -> {
            return (replace(Regex("[^\\d]"), "").toBigDecimalOrNull() ?: 0) as T
        }
        Float::class -> {
            return (replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0) as T
        }
        Double::class -> {
            return (replace(Regex("[^\\d.]"), "").toDoubleOrNull() ?: 0) as T
        }
        else -> 0 as T
    }
}
