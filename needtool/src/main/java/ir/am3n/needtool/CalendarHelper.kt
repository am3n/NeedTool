package ir.am3n.needtool

import ir.hamsaa.persiandatepicker.util.PersianCalendar
import java.util.*

/**
 * 1      18:30 - 7:30
 * 2      19:00 - 6:30
 * 3,4    19:30 - 6:00
 * 5      19:00 - 6:30
 * 6      18:30 - 7:30
 * 7      17:00 - 6:30
 * 8      16:30 - 7:00
 * 9,10   16:00 - 7:30
 * 11     16:30 - 7:00
 * 12     17:00 - 6:30
 */
fun isDay(): Boolean {

    val u_06_00 = (6 * 60 * 60 * 1000).toLong()
    val u_06_30 = u_06_00 + (30 * 60 * 1000)
    val u_07_00 = u_06_30 + (30 * 60 * 1000)
    val u_07_30 = u_07_00 + (30 * 60 * 1000)

    val u_16_00 = (16 * 60 * 60 * 1000).toLong()
    val u_16_30 = u_16_00 + (30 * 60 * 1000)
    val u_17_00 = u_16_30 + (30 * 60 * 1000)
    val u_17_30 = u_17_00 + (30 * 60 * 1000)
    val u_18_00 = u_17_30 + (30 * 60 * 1000)
    val u_18_30 = u_18_00 + (30 * 60 * 1000)
    val u_19_00 = u_18_30 + (30 * 60 * 1000)
    val u_19_30 = u_19_00 + (30 * 60 * 1000)

    val c = PersianCalendar()
    val unixOfDay = (c[Calendar.HOUR_OF_DAY] * 60 * 60 * 1000) +
            (c[Calendar.MINUTE] * 60 * 1000) +
            (c[Calendar.SECOND] * 1000) +
            (c[Calendar.MILLISECOND])

    return when (c.persianMonth) {
        1 -> unixOfDay in (u_07_30 + 1) until u_18_30
        2 -> unixOfDay in (u_06_30 + 1) until u_19_00
        3, 4 -> unixOfDay in (u_06_00 + 1) until u_19_30
        5 -> unixOfDay in (u_06_30 + 1) until u_19_00
        6 -> unixOfDay in (u_07_30 + 1) until u_18_30
        7 -> unixOfDay in (u_06_30 + 1) until u_17_00
        8 -> unixOfDay in (u_07_00 + 1) until u_16_30
        9, 10 -> unixOfDay in (u_07_30 + 1) until u_16_00
        11 -> unixOfDay in (u_07_00 + 1) until u_16_30
        12 -> unixOfDay in (u_06_30 + 1) until u_17_00
        else -> true
    }

}