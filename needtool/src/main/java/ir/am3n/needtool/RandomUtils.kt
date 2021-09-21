package ir.am3n.needtool

import java.util.*
import kotlin.random.Random


/**
 * @param range random number range
 * @param decimal The number of decimal points, 0 <= decimal <=6, 0 is not a decimal point.
 **/
fun Random.floatNum(range: Int, decimal: Int = 2): Float {
    val floatRandomNum = Random.nextInt(range) + Random.nextFloat()
    // 0 without a decimal point, 6 with 6 decimal places, not both in this range are 2 decimal points
    val mDecimal = if (decimal in 0..6) decimal else 2
    return String.format(Locale.US, "%." + mDecimal + "f", floatRandomNum).toFloat()
}