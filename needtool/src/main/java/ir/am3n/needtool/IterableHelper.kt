package ir.am3n.needtool

import androidx.core.util.Predicate
import java.io.BufferedReader


fun Iterable<String?>.filterNotEmpty(): MutableList<String> {
    val destination = mutableListOf<String>()
    for (element in this)
        if (element != null && element.toString().trim().isNotEmpty()) destination.add(element)
    return destination
}


val BufferedReader.lines: Iterator<String?> get() = object : Iterator<String?> {
    var line: String? = this@lines.readLine()
    override fun next(): String? {
        val lastLine = line
        line = this@lines.readLine()
        return lastLine
    }
    override fun hasNext() = line != null
}


