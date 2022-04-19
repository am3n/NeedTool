package ir.am3n.needtool

import android.content.Context
import java.util.*
import kotlin.experimental.and

class Num2Persian(private val value: Any) {

    private val delimiter = " و "
    private val zero = "صفر"
    private val negative = "منفی "

    private val letters: Array<Array<String>> = arrayOf(
        arrayOf("", "یک", "دو", "سه", "چهار", "پنج", "شش", "هفت", "هشت", "نه"),
        arrayOf("ده", "یازده", "دوازده", "سیزده", "چهارده", "پانزده", "شانزده", "هفده", "هجده", "نوزده", "بیست"),
        arrayOf("", "", "بیست", "سی", "چهل", "پنجاه", "شصت", "هفتاد", "هشتاد", "نود"),
        arrayOf("", "یکصد", "دویست", "سیصد", "چهارصد", "پانصد", "ششصد", "هفتصد", "هشتصد", "نهصد"),
        arrayOf(
            "", " هزار", " میلیون", " میلیارد", " بیلیون", " بیلیارد", " تریلیون", " تریلیارد", "کوآدریلیون",
            " کادریلیارد", " کوینتیلیون", " کوانتینیارد", " سکستیلیون", " سکستیلیارد", " سپتیلیون", "سپتیلیارد",
            " اکتیلیون", " اکتیلیارد", " نانیلیون", " نانیلیارد", " دسیلیون", " دسیلیارد"
        )
    )

    private val decimalSuffixes: Array<String> = arrayOf(
        "", "دهم", "صدم", "هزارم", "ده‌هزارم", "صد‌هزارم", "میلیونوم", "ده‌میلیونوم", "صدمیلیونوم", "میلیاردم", "ده‌میلیاردم", "صد‌‌میلیاردم"
    )


    private fun prepareNumber(num: Any): List<String> {
        var out: Any = num
        if (out is Number) {
            out = out.toString()
        }
        out = out as String
        val numLen = out.length % 3
        if (numLen == 1) {
            out = "00${out}"
        } else if (numLen == 2) {
            out = "0${out}"
        }
        return splitStringBySize(out, 3)
    }

    private fun splitStringBySize(str: String, size: Int): List<String> {
        val split = mutableListOf<String>()
        for (i in 0 until (str.length / size)) {
            split.add(str.substring(i * size, ((i + 1) * size).coerceAtMost(str.length)))
        }
        return split
    }

    private fun threeNumbersToLetter(num: String): String {
        if (num == "") {
            return ""
        }
        val parsedInt = Integer.parseInt(num)
        if (parsedInt < 10) {
            return letters[0][parsedInt]
        }
        if (parsedInt < 20) {
            return letters[1][parsedInt - 10]
        }
        if (parsedInt < 100) {
            val one = parsedInt % 10
            val ten = (parsedInt - one) / 10
            if (one > 0) {
                return letters[2][ten] + delimiter + letters[0][one]
            }
            return letters[2][ten]
        }
        val one = parsedInt % 10
        val hundreds = (parsedInt - (parsedInt % 100)) / 100
        val ten = (parsedInt - ((hundreds * 100) + one)) / 10
        var out = arrayOf(letters[3][hundreds])
        val secondPart = ((ten * 10) + one)
        if (secondPart > 0) {
            if (secondPart < 10) {
                out = out.plus(letters[0][secondPart])
            } else if (secondPart <= 20) {
                out = out.plus(letters[1][secondPart - 10])
            } else {
                out = out.plus(letters[2][ten])
                if (one > 0) {
                    out = out.plus(letters[0][one])
                }
            }
        }
        return out.joinToString(delimiter)
    }

    private fun convertDecimalPart(dp: String): String {
        var decimalPart = dp.replace(Regex("0*$"), "")
        if (decimalPart == "") {
            return ""
        }
        if (decimalPart.length > 11) {
            decimalPart = decimalPart.substring(0, 11)
        }
        return " ممیز " + Num2Persian(decimalPart).get() + " " + decimalSuffixes[decimalPart.length]
    }

    fun get(): String {
        // Clear Non digits
        var input = value.toString().replace(Regex("[^\\d.]"), "") //    /[^0-9.-]/g
        var isNegative = false
        val doubleParse = input.toDoubleOrNull() ?: return zero
        // check for zero
        if (doubleParse == 0.toDouble()){
            return zero
        }
        // set negative flag:true if the number is less than 0
        if (doubleParse < 0){
            isNegative = true
            input = input.replace(Regex("[\\-]"), "")
        }

        // Declare Parts
        var decimalPart = ""
        var integerPart = input
        val pointIndex = input.indexOf('.')
        // Check for float numbers form string and split Int/Dec
        if (pointIndex > -1) {
            integerPart = input.substring(0, pointIndex)
            decimalPart = input.substring(pointIndex + 1, input.length)
        }

        if (integerPart.length > 66) {
            return "خارج از محدوده"
        }

        // Split to sections
        val slicedNumber = prepareNumber(integerPart)
        // Fetch Sections and convert
        var output = arrayOf<String>()
        val splitLen = slicedNumber.size
        for (index in 0 until splitLen) {
            val sectionTitle = letters[4][splitLen - (index + 1)]
            val converted = threeNumbersToLetter(slicedNumber[index])
            if (converted !== "") {
                output = output.plus(converted + sectionTitle)
            }
        }

        // Convert Decimal part
        if (decimalPart.isNotEmpty()) {
            decimalPart = convertDecimalPart(decimalPart)
        }

        return (if(isNegative) negative else "") + output.joinToString(delimiter) + decimalPart
    }

}

fun String.persianLetter(): String = Num2Persian(this).get()
fun Number.persianLetter(): String = this.toString().persianLetter()

fun String.persianNth(): String = (Num2Persian(this).get() + "م").replace("سهم", "سوم")
fun Number.persianNth(): String = this.toString().persianNth()


val String.d2: String get() = String.format(Locale.US, "%02d", this.toInt())
val Int.d2: String get() = String.format(Locale.US, "%02d", this)


fun String.rtlSignNum(context: Context?): String {
    return if (context?.resources?.isRtl == true && this.intOr0 < 0)
        this.replace("-", "") + "-"
    else this
}


val String.persianOrArabicToEnglish: String get() {
    var engNumStr = this.replace("۰".toRegex(), "0")
    engNumStr = engNumStr.replace("۱".toRegex(), "1")
    engNumStr = engNumStr.replace("۲".toRegex(), "2")
    engNumStr = engNumStr.replace("۳".toRegex(), "3")
    engNumStr = engNumStr.replace("۴".toRegex(), "4")
    engNumStr = engNumStr.replace("۵".toRegex(), "5")
    engNumStr = engNumStr.replace("۶".toRegex(), "6")
    engNumStr = engNumStr.replace("۷".toRegex(), "7")
    engNumStr = engNumStr.replace("۸".toRegex(), "8")
    engNumStr = engNumStr.replace("۹".toRegex(), "9")
    // arabic
    engNumStr = engNumStr.replace("٠".toRegex(), "0")
    engNumStr = engNumStr.replace("١".toRegex(), "1")
    engNumStr = engNumStr.replace("٢".toRegex(), "2")
    engNumStr = engNumStr.replace("٣".toRegex(), "3")
    engNumStr = engNumStr.replace("٤".toRegex(), "4")
    engNumStr = engNumStr.replace("٥".toRegex(), "5")
    engNumStr = engNumStr.replace("٦".toRegex(), "6")
    engNumStr = engNumStr.replace("٧".toRegex(), "7")
    engNumStr = engNumStr.replace("٨".toRegex(), "8")
    engNumStr = engNumStr.replace("٩".toRegex(), "9")

    engNumStr = engNumStr.replace("[٬،]".toRegex(), ",")

    return engNumStr
}


val Int.toPersian: String get() {
    return toFloat().toPersian.replace(Regex("\\..*"), "")
}

val Float.toPersian: String get() {
    return toString().toDouble().toPersian
}

val Double.toPersian: String get() {
    return toString().toPersian
}

val Long.toPersian: String get() {
    return toString().toPersian
}

val String.toPersian: String get() {
    var persianNumStr = replace("0".toRegex(), "۰")
    persianNumStr = persianNumStr.replace("1".toRegex(), "۱")
    persianNumStr = persianNumStr.replace("2".toRegex(), "۲")
    persianNumStr = persianNumStr.replace("3".toRegex(), "۳")
    persianNumStr = persianNumStr.replace("4".toRegex(), "۴")
    persianNumStr = persianNumStr.replace("5".toRegex(), "۵")
    persianNumStr = persianNumStr.replace("6".toRegex(), "۶")
    persianNumStr = persianNumStr.replace("7".toRegex(), "۷")
    persianNumStr = persianNumStr.replace("8".toRegex(), "۸")
    persianNumStr = persianNumStr.replace("9".toRegex(), "۹")
    persianNumStr = persianNumStr.replace(",".toRegex(), "٬")
    return persianNumStr
}


fun Int?.ifNullOrZero(defaultValue: Int): Int {
    return if (this == null || this == 0) defaultValue else this
}

fun Long?.ifNullOrZero(defaultValue: Long): Long {
    return if (this == null || this == 0L) defaultValue else this
}

fun Float?.ifNullOrZero(defaultValue: Float): Float {
    return if (this == null || this == 0f) defaultValue else this
}

fun Double?.ifNullOrZero(defaultValue: Double): Double {
    return if (this == null || this == 0.0) defaultValue else this
}

