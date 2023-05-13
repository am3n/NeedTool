package ir.am3n.needtool

import java.io.Serializable


data class MutablePair<A, B>(
    var first: A,
    var second: B,
) : Serializable {
    /**
     * Returns string representation of the [Pair] including its [first] and [second] values.
     */
    override fun toString(): String = "($first, $second)"
}

data class MutableTriple<A, B, C>(
    var first: A,
    var second: B,
    var third: C,
) : Serializable {
    /**
     * Returns string representation of the [Triple] including its [first], [second] and [third] values.
     */
    override fun toString(): String = "($first, $second, $third)"
}

