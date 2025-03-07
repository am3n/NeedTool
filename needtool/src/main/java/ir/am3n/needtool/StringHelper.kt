package ir.am3n.needtool

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Editable
import androidx.core.content.ContextCompat
import java.math.BigDecimal
import java.math.BigInteger


const val loremIpsumShort = "لورم ایپسوم متن ساختگی با تولید سادگی نامفهوم از صنعت چاپ، و با استفاده از طراحان گرافیک است."


val Editable?.len: Int get() = this?.length ?: -1
val String?.len: Int get() = this?.length ?: -1

fun String?.ifBlank(defaultValue: String? = null): String? {
    return if (this?.isBlank() == true) defaultValue else this
}

fun String?.ifNullOrBlank(defaultValue: String): String {
    return if (isNullOrBlank()) defaultValue else this
}


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
            (replace(Regex("\\D"), "").toShortOrNull() ?: 0) as T
        }
        Int::class -> {
            (replace(Regex("\\D"), "").toIntOrNull() ?: 0) as T
        }
        Long::class -> {
            (replace(Regex("\\D"), "").toLongOrNull() ?: 0) as T
        }
        BigInteger::class -> {
            (replace(Regex("\\D"), "").toBigIntegerOrNull() ?: 0) as T
        }
        BigDecimal::class -> {
            (replace(Regex("\\D"), "").toBigDecimalOrNull() ?: 0) as T
        }
        Float::class -> {
            (replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0) as T
        }
        Double::class -> {
            (replace(Regex("[^\\d.]"), "").toDoubleOrNull() ?: 0) as T
        }
        else -> {
            replace(Regex("\\D"), "") as T
        }
    }
}


fun change2by2(str: String): ByteArray {
    return change2by2(str.toByteArray(Charsets.UTF_8))
}


val String?.intOr0: Int get() = this?.toIntOrNull() ?: 0

val String?.intOr0AsString: String get() = intOr0.toString()


fun String.optSubString(startIndex: Int, endIndex: Int): String {
    return try {
        substring(startIndex, endIndex) + ".."
    } catch (t: Throwable) {
        this
    }
}

fun String.optSubString(range: IntRange): String {
    return try {
        substring(range) + ".."
    } catch (t: Throwable) {
        this
    }
}

fun String.replaceArabicToPersian(): String {
    var result = this
    mapOf(
        "ك" to "ک",
        "دِ" to "د",
        "بِ" to "ب",
        "زِ" to "ز",
        "ذِ" to "ذ",
        "شِ" to "ش",
        "سِ" to "س",
        "ة" to "ه",
        "ى" to "ی",
        "ي" to "ی",
        "١" to "۱",
        "٢" to "۲",
        "٣" to "۳",
        "٤" to "۴",
        "٥" to "۵",
        "٦" to "۶",
        "٧" to "۷",
        "٨" to "۸",
        "٩" to "۹",
        "٠" to "۰",
    ).forEach { entry ->
        result = result.replace(entry.key, entry.value)
    }
    return result
}
