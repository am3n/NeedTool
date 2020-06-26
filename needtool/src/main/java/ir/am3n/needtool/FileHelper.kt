package ir.am3n.needtool

import java.io.File


val File.sizeInBytes get() = this.length()

val File.sizeInKB get() = this.sizeInBytes / 1024

val File.sizeInMB get() = this.sizeInKB / 1024


val String.lastPathComponent: String
    get() {
        var path = this
        if (path.endsWith("/"))
            path = path.substring(0, path.length - 1)
        var index = path.lastIndexOf('/')
        if (index < 0) {
            if (path.endsWith("\\"))
                path = path.substring(0, path.length - 1)
            index = path.lastIndexOf('\\')
            if (index < 0)
                return path
        }
        return path.substring(index + 1)
    }
