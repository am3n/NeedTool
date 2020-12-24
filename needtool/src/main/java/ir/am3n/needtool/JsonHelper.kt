package ir.am3n.needtool

import org.json.JSONArray
import org.json.JSONObject


fun removeRecursive(obj: Any, remove: Array<String>) {
    if (obj is JSONArray) {
        val array: JSONArray = obj
        for (i in 0 until array.length())
            removeRecursive(array.get(i), remove)

    } else if (obj is JSONObject) {
        val json: JSONObject = obj
        val names: JSONArray = json.names() ?: return
        for (i in 0 until names.length()) {
            val key: String = names.getString(i)
            if (remove.contains(key)) {
                json.remove(key)
            } else {
                removeRecursive(json.get(key), remove)
            }
        }
    }
}


fun objectToValue(obj: Any, conv: String) {
    if (obj is JSONArray) {
        val array: JSONArray = obj
        for (i in 0 until array.length())
            objectToValue(array.get(i), conv)

    } else if (obj is JSONObject) {
        val json: JSONObject = obj
        val names: JSONArray = json.names() ?: return
        for (i in 0 until names.length()) {
            val key: String = names.getString(i)
            if (json.get(key) is JSONObject && (json.get(key) as JSONObject).has(conv)) {
                json.put(key, (json.get(key) as JSONObject).get(conv))
            } else {
                objectToValue(json.get(key), conv)
            }
        }
    }
}


fun String.isJson(): Boolean {
    try {
        val obj = JSONObject(this)
        return obj!=null
    } catch (t: Throwable) {}
    try {
        val arr = JSONArray(this)
        return arr!=null
    } catch (t: Throwable) {}
    return false
}
