package ir.am3n.needtool

import java.security.MessageDigest

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