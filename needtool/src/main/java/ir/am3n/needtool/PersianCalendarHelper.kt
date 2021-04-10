package ir.am3n.needtool

import ir.hamsaa.persiandatepicker.util.PersianCalendar
import java.util.*

fun PersianCalendar.persianDateTimeVerbose(): String {
    val dow = this.persianWeekDayName
    val day = this.persianDay
    val month = this.persianMonthName
    val year = this.persianYear
    val time = "ساعت"
    val hour = String.format(Locale.US, "%02d", this.get(Calendar.HOUR_OF_DAY))
    val mint = String.format(Locale.US, "%02d", this.get(Calendar.MINUTE))
    return "$dow $day $month $year $time $hour:$mint"
}

fun PersianCalendar.persianDateTime(): String {
    return persianDate() + " " + persianTime()
}

fun PersianCalendar.persianDate(): String {
    val day = String.format(Locale.US, "%02d", this.persianDay)
    val month = String.format(Locale.US, "%02d", this.persianMonth)
    val year = String.format(Locale.US, "%04d", this.persianYear)
    return "$year/$month/$day"
}

fun PersianCalendar.persianTime(): String {
    val hour = String.format(Locale.US, "%02d", this.get(Calendar.HOUR_OF_DAY))
    val minute = String.format(Locale.US, "%02d", this.get(Calendar.MINUTE))
    val second = String.format(Locale.US, "%02d", this.get(Calendar.SECOND))
    return "$hour:$minute:$second"
}

fun PersianCalendar.persianUnixtime(): Long {
    return this.timeInMillis + TimeZone.getDefault().getOffset(this.time.time)
}