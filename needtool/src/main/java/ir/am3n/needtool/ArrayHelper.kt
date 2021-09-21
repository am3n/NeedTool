package ir.am3n.needtool

import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.reflect.KFunction0

/*public inline fun <T, R> Array<out T>.map(transform: (T) -> R): List<R> {
    return mapTo(ArrayList<R>(size), transform)
}*/

/*fun <T> T.clone(): T {
    val byteArrayOutputStream = ByteArrayOutputStream()
    ObjectOutputStream(byteArrayOutputStream).use { outputStream ->
        outputStream.writeObject(this)
    }

    val bytes = byteArrayOutputStream.toByteArray()
    ObjectInputStream(ByteArrayInputStream(bytes)).use { inputStream ->
        return inputStream.readObject() as T
    }
}*/

/*fun <T : Cloneable> Array<T>.duplicate() {
    map().toTypedArray()
}*/

/*private fun <T> Array<T>.map(transform: KFunction0<Array<T>>) {
    TODO("Not yet implemented")
}*/



fun <T> Array<T>.asJsonArray(): JSONArray {
    return map { it.toString() }.toTypedArray().asJsonArray()
}
fun Array<String>.asJsonArray(): JSONArray {
    return try {
        JSONObject("""{"data":[${joinToString(",")}]}""").getJSONArray("data")
    } catch (t: Throwable) {
        JSONArray()
    }
}

