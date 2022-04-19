package ir.am3n.needtool

import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.Observable


fun postLiveEventBus(key: String, value: Any? = null) {
    LiveEventBus.get<Any>(key).post(value)
}

fun getLiveEventBus(key: String): Observable<Any> {
    return LiveEventBus.get(key, Any::class.java)
}
