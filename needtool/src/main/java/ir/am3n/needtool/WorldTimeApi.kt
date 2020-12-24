package ir.am3n.needtool

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class WorldTimeApi(timezone: String? = "Asia/Tehran") {

    private val url = URL("http://worldtimeapi.org/api/timezone/$timezone")

    fun get(): Response {

        val now = System.currentTimeMillis()
        with(url.openConnection() as HttpURLConnection) {
            connectTimeout = 3000
            readTimeout = 3000
            requestMethod = "GET"

            val data = inputStream.bufferedReader().readLine()
            val latency = System.currentTimeMillis() - now
            val json = JSONObject(data)

            val datetime = json.getString("datetime").split("T")
            val date = datetime[0].split("-")
            val time = datetime[1].split(".")[0].split(":")
            val timeMillis = datetime[1].split(".")[1].substring(0, 3)
            val c = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            c.set(date[0].toInt(), date[1].toInt()-1, date[2].toInt(), time[0].toInt(), time[1].toInt(), time[2].toInt())
            c.set(Calendar.MILLISECOND, timeMillis.toInt())

            return Response(
                json.getInt("week_number"),
                json.getString("utc_offset"),
                json.getString("utc_datetime"),
                json.getLong("unixtime"),
                json.getString("timezone"),
                json.getInt("raw_offset"),
                json.optInt("dst_until"),
                json.optInt("dst_offset"),
                json.optInt("dst_from"),
                json.getBoolean("dst"),
                json.getInt("day_of_year"),
                json.getInt("day_of_week"),
                json.getString("datetime"),
                c.time.time,
                latency / 2,
                json.getString("client_ip"),
                json.getString("abbreviation")
            )
        }
    }

    @Keep
    data class Response (

        @Expose @SerializedName("week_number")
        val weekNumber: Int?,

        @Expose @SerializedName("utc_offset")
        val utcOffset: String?,

        @Expose @SerializedName("utc_datetime")
        val utcDatetime: String?,

        @Expose @SerializedName("utc_unixtime")
        val utcUnixtime: Long?,

        @Expose @SerializedName("timezone")
        val timezone: String?,

        @Expose @SerializedName("raw_offset")
        val rawOffset: Int?,

        @Expose @SerializedName("dst_until")
        val dstUntil: Int?,

        @Expose @SerializedName("dst_offset")
        val dstOffset: Int?,

        @Expose @SerializedName("dst_from")
        val dstFrom: Int?,

        @Expose @SerializedName("dst")
        val dst: Boolean?,

        @Expose @SerializedName("day_of_year")
        val dayOfYear: Int?,

        @Expose @SerializedName("day_of_week")
        val dayOfWeek: Int?,

        @Expose @SerializedName("datetime")
        val datetime: String?,

        @Expose @SerializedName("unixtime")
        val unixtime: Long?,

        @Expose @SerializedName("latency_in_millis")
        val latencyInMillis: Long?,

        @Expose @SerializedName("client_ip")
        val clientIp: String?,

        @Expose @SerializedName("abbreviation")
        val abbreviation: String?

    ) {

        val unixtimeWithLatency: Long get() = latencyInMillis?.let { unixtime?.plus(it) } ?: 0L

    }

}