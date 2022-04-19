package ir.am3n.needtool


inline fun <reified T> createClass(): T {
    return T::class.java.newInstance()
}
