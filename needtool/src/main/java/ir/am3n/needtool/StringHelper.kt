package ir.am3n.needtool

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import java.math.BigDecimal
import java.math.BigInteger

val String.url2Host get() = replace(Regex(".*//"), "").replace(Regex("/.*"), "").replace("www.", "")

fun String.firstCap() = replaceFirstChar { it.uppercase() }

fun String.isNumeric() = toLongOrNull() != null

fun String.isAlphabet() = matches(Regex("[a-zA-Z]+"))

fun String.isAlphabetAndNumber() = matches(Regex("[a-zA-Z0-9]+")) && !isNumeric() && !isAlphabet()

fun String.isPhoneNumber() = isNumeric() &&
    ((startsWith("09") && length == 11) ||
        (startsWith("989") && length == 12) ||
        (startsWith("+989") && length == 13))

fun String.replaceLast(delimiter: String, replace: String): String {
    val index = lastIndexOf(delimiter)
    return this.replaceRange(index, index + delimiter.length, replace)
}


fun String?.asUri(): Uri? {
    try {
        return Uri.parse(this)
    } catch (e: Exception) {
    }
    return null
}

fun Uri?.openInBrowser(context: Context) {
    this ?: return // Do nothing if uri is null
    val browserIntent = Intent(Intent.ACTION_VIEW, this)
    ContextCompat.startActivity(context, browserIntent, null)
}


inline fun <reified T : Any> String.removeNoneNumeric(): T {
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


fun change2by2(str: String): ByteArray {
    return change2by2(str.toByteArray(Charsets.UTF_8))
}


val String.intOr0: Int get() = toIntOrNull() ?: 0

val String.intOr0Str: String get() = intOr0.toString()

