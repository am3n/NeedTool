package ir.am3n.needtool.polygon

/**
 * Point on 2D landscape
 *
 * @author Roman Kushnarenko (sromku@gmail.com)
 */
data class Point(var x: Double, var y: Double) {

    override fun toString(): String {
        return String.format("(%f, %f)", x, y)
    }

}