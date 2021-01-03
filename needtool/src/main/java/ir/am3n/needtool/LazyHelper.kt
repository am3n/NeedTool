package ir.am3n.needtool

import java.io.Serializable
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * var i = 0
 * var test by cached {
 *   "test_${i++}"
 * }
 * println("print: $test")
 * test = null
 * println("print: $test")
 */


interface Cached<T> {
    /**
     * Gets the lazily initialized value of the current Cached instance.
     * Once the value was initialized it must not change during the rest of lifetime of this Cached instance.
     */
    var value: T?
    /**
     * Returns `true` if a value for this Cached instance has a value, and `false` otherwise.
     */
    fun isCached(): Boolean

    operator fun setValue(thisRef: Any?, property: KProperty<*>, new_value: T?) {
        value = new_value
    }

    /*operator fun <T> Cached<T>.setValue(thisRef: Any?, property: KProperty<*>, new_value: T?) {
        value = new_value
    }*/

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }

    /*operator fun <T> setValue(scratch: Any, property: KProperty<T?>, t: T?) {
        value = t
    }*/

}

fun <T> cached(initializer: () -> T): Cached<T> = SynchronizedCachedImpl(initializer)

//operator fun <T> Cached<T>.getValue(thisRef: Any?, property: KProperty<*>): T? = value
//operator fun <T> Cached<T>.setValue(thisRef: Any?, property: KProperty<*>, new_value: T?) { value = new_value }

private class SynchronizedCachedImpl<T>(initializer: () -> T, lock: Any? = null) : Cached<T>, Serializable {
    private var initializer: (() -> T)? = initializer
    @Volatile private var _value: Any? = null
    // final field is required to enable safe publication of constructed instance
    private val lock = lock ?: this

    override var value: T?
        set(new_value) {
            synchronized(lock){
                _value = new_value
            }
        }
        get() {
            val _v1 = _value
            if (_v1 !== null) {
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== null) {
                    _v2 as T
                }
                else {
                    val typedValue = initializer!!()
                    _value = typedValue
//                    initializer = null
                    typedValue
                }
            }
        }

    override fun isCached(): Boolean = _value !== null

    override fun toString(): String = if (isCached()) value.toString() else "Cached value is not set yet."

//    private fun writeReplace(): Any = InitializedLazyImpl(value)
}





//**************************************************************************************************
//**************************************************************************************************






/** how to use resettableLazy
 * var i = 0
 * val manager = ResettableLazyManager()
 * val test: String? by resettableLazy(manager) {
 *   "test_${i++}"
 * }
 * println("print: $test")
 * manager.reset()
 * println("print: $test")
 */

class ResettableLazyManager {
    val managedDelegates = LinkedList<Resettable>()
    fun register(managed: Resettable) {
        synchronized (managedDelegates) {
            managedDelegates.add(managed)
        }
    }
    fun reset() {
        synchronized (managedDelegates) {
            managedDelegates.forEach { it.reset() }
            managedDelegates.clear()
        }
    }
}

interface Resettable {
    fun reset()
}

class ResettableLazy<PROPTYPE>(val manager: ResettableLazyManager, val init: ()->PROPTYPE): Resettable {
    @Volatile var lazyHolder = makeInitBlock()
    operator fun getValue(thisRef: Any?, property: KProperty<*>): PROPTYPE {
        return lazyHolder.value
    }
    override fun reset() {
        lazyHolder = makeInitBlock()
    }
    fun makeInitBlock(): Lazy<PROPTYPE> {
        return lazy {
            manager.register(this)
            init()
        }
    }
}

fun <T> resettableLazy(manager: ResettableLazyManager, init: ()->T): ResettableLazy<T> {
    return ResettableLazy(manager, init)
}

fun resettableManager(): ResettableLazyManager = ResettableLazyManager()





//**************************************************************************************************
//**************************************************************************************************




/** how to use renewableLazy
 * var i = 0
 * var test: String? by renewableLazy { p, kProperty ->
 *    "i: ${i++}"
 * }
 * println("print: $test")
 * //test = null
 * println("print: $test")
 */


fun <P, T> renewableLazy(initializer: (P) -> T): ReadWriteProperty<P, T> =
    RenewableSynchronizedLazyWithThisImpl({ t, _ ->
        initializer.invoke(t)
    })

fun <P, T> renewableLazy(initializer: (P, KProperty<*>) -> T): ReadWriteProperty<P, T> =
    RenewableSynchronizedLazyWithThisImpl(initializer)

class RenewableSynchronizedLazyWithThisImpl<in T, V>(
    val initializer: (T, KProperty<*>) -> V,
    private val lock: Any = {}
) : ReadWriteProperty<T, V> {
    @Volatile
    private var _value: Any? = null
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        val _v1 = _value
        if (_v1 !== null) {
            @Suppress("UNCHECKED_CAST")
            return _v1 as V
        }
        return synchronized(lock) {
            val _v2 = _value
            if (_v2 !== null) {
                @Suppress("UNCHECKED_CAST") (_v2 as V)
            } else {
                val typedValue = initializer(thisRef, property)
                _value = typedValue
                typedValue
            }
        }
    }
    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        synchronized(lock) {
            _value = null
        }
    }
}






//**************************************************************************************************
//**************************************************************************************************


/**
 * var i = 0
 * val lazy = MutableLazy.resettableLazy {
 *     "test_${i++}"
 * }
 * val test by lazy
 * println("print: $test")
 * lazy.reset()
 * println("print: $test")
 */

class MutableLazy<T>(private val initializer: () -> T) : Lazy<T> {
    private var cached: T? = null
    override val value: T
        get() {
            if (cached == null) {
                cached = initializer()
            }
            @Suppress("UNCHECKED_CAST")
            return cached as T
        }

    fun reset() {
        cached = null
    }

    override fun isInitialized(): Boolean = cached != null

    companion object {
        fun <T> resettableLazy(value: () -> T) = MutableLazy(value)
    }
}