package ir.am3n.needtool

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/* error
val String.crc32: String
    get() {
        val bytes = MessageDigest.getInstance("CRC32").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }*/

/* error
val String.md2: String
    get() {
        val bytes = MessageDigest.getInstance("MD2").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }*/

/* error
val String.md4: String
    get() {
        val bytes = MessageDigest.getInstance("MD4").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }*/

val String.md5: String
    get() {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

val String.sha1: String
    get() {
        val bytes = MessageDigest.getInstance("SHA-1").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

val String.sha256: String
    get() {
        val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

val String.sha512: String
    get() {
        val bytes = MessageDigest.getInstance("SHA-512").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }


fun encrypt(plaintext: String, key: String): String? {
    return try {
        val skeySpec = SecretKeySpec(change2by2(key), "AES/GCM/NOPADDING")
        val cipher = Cipher.getInstance("AES/GCM/NOPADDING")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        Base64.encodeToString(encrypted, Base64.DEFAULT)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}

fun decrypt(ciphertext: String, key: String): String? {
    return try {
        val skeySpec = SecretKeySpec(change2by2(key), "AES/GCM/NOPADDING")
        val cipher = Cipher.getInstance("AES/GCM/NOPADDING")
        cipher.init(Cipher.DECRYPT_MODE, skeySpec)
        val decrypted = cipher.doFinal(Base64.decode(ciphertext, Base64.DEFAULT))
        String(decrypted, Charsets.UTF_8)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}



