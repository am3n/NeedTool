package ir.am3n.needtool

import java.util.regex.Pattern


fun String?.isValidEmail(): Boolean {
    val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,8}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(this ?: "")
    return matcher.matches()
}

