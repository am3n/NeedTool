package ir.am3n.needtool

open class SingletonHolder<out T: Any, in A>(creator: (A?) -> T) {

    private var creator: ((A?) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun cOr(arg: A?): T {

        val i = instance

        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}