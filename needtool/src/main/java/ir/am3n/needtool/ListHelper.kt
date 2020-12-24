package ir.am3n.needtool

import androidx.core.util.Predicate


inline fun <reified T> List<*>.asListOf(): List<T>? =
    if (all { it is T })
        @Suppress("UNCHECKED_CAST")
        this as List<T>
    else
        null


fun <T> List<T>?.removeIF(filter: Predicate<T>): List<T> {
    val list = mutableListOf<T>()
    try {
        val each: Iterator<T> = this!!.iterator()
        while (each.hasNext()) {
            val item = each.next()
            if (!filter.test(item as T?)) {
                list.add(item)
            }
        }
        return list
    } catch (t: Throwable) {}
    return emptyList()
}


fun <T, U> List<T>.intersect(uList: List<U>?, filterPredicate : (T, U) -> Boolean) =
    filter { m -> uList?.any { filterPredicate(m, it)} == true }

