package ir.am3n.needtool

import android.content.Context
import android.util.Log
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import okhttp3.ResponseBody
import java.io.*


class BooleanIntAdapter : TypeAdapter<Boolean?>() {

    @Throws(IOException::class)
    override fun write(jw: JsonWriter, value: Boolean?) {
        if (value == null) {
            jw.nullValue()
        } else {
            jw.value(value)
        }
    }

    @Throws(IOException::class)
    override fun read(jr: JsonReader): Boolean? {
        return when (val peek: JsonToken = jr.peek()) {
            JsonToken.BOOLEAN -> jr.nextBoolean()
            JsonToken.NULL -> {
                jr.nextNull()
                null
            }
            JsonToken.NUMBER -> jr.nextInt() != 0
            JsonToken.STRING -> jr.nextString().toBooleanOrNull()
            else -> throw IllegalStateException("Expected BOOLEAN or NUMBER but was $peek")
        }
    }

}

fun saveBodyToFile(context: Context?, body: ResponseBody?, name: String, onProgress: (Int) -> Unit = {}): File? {
    return try {

        val tempFile = File(context?.filesDir?.absolutePath + File.separator.toString() + name)
        if (tempFile.exists()) {
            tempFile.deleteOnExit()
            tempFile.createNewFile()
        }

        var ins: InputStream? = null
        var ous: OutputStream? = null

        try {

            val fileReader = ByteArray(4096)
            val fileSize: Long = body?.contentLength() ?: 0L
            var downloaded: Long = 0
            ins = body?.byteStream()
            ous = FileOutputStream(tempFile)

            while (true) {
                val read: Int = ins?.read(fileReader) ?: -1
                if (read == -1) {
                    break
                }
                ous.write(fileReader, 0, read)
                downloaded += read.toLong()
                onProgress((downloaded.toDouble() / fileSize * 100).toInt())
                Log.d("TAG", "file download: $downloaded of $fileSize")
            }

            if (downloaded == fileSize) {
                tempFile
            } else {
                tempFile.delete()
                null
            }

        } catch (e: Exception) {
            tempFile.delete()
            null
        } finally {
            ins?.close()
            ous?.close()
        }

    } catch (e: Exception) {
        null
    }
}
