package ir.am3n.needtool

import android.animation.ValueAnimator
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.Size
import kotlin.math.abs

abstract class CCFAnimator protected constructor(fromColor: Int, toColor: Int) {

    interface OnNewColorListener {
        fun onNewColor(@ColorInt color: Int)
    }

    /**
     * Returns a color depending on fraction
     * @param fraction current animation fraction
     * @return color
     */
    abstract fun getColor(@FloatRange(from = 0.0, to = 1.0) fraction: Float): Int

    fun asValueAnimator(onNewColorListener: OnNewColorListener): ValueAnimator {
        val animator = ValueAnimator.ofFloat(.0f, 1f)
        animator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction
            onNewColorListener.onNewColor(getColor(fraction))
        }
        return animator
    }

    protected interface AlphaEvaluator {
        fun evaluate(fraction: Float): Int
    }

    protected class AlphaEvaluatorImpl(private val mFromAlpha: Int, private val mToAlpha: Int) : AlphaEvaluator {
        override fun evaluate(fraction: Float): Int {
            return (mFromAlpha + (mToAlpha - mFromAlpha) * fraction + .5f).toInt()
        }
    }

    protected class RGBAnimator(
        private val mAlphaEvaluator: AlphaEvaluator?,
        @ColorInt fromColor: Int,
        @ColorInt toColor: Int
    ) : CCFAnimator(fromColor, toColor) {
        private val mFromColor: IntArray = buildRGB(fromColor)
        private val mToColor: IntArray = buildRGB(toColor)
        private val mOut: IntArray = IntArray(3)
        override fun getColor(fraction: Float): Int {
            mOut[0] = (mFromColor[0] + ((mToColor[0] - mFromColor[0]) * fraction + .5f)).toInt()
            mOut[1] = (mFromColor[1] + ((mToColor[1] - mFromColor[1]) * fraction + .5f)).toInt()
            mOut[2] = (mFromColor[2] + ((mToColor[2] - mFromColor[2]) * fraction + .5f)).toInt()
            return if (mAlphaEvaluator != null) {
                argbToColor(mAlphaEvaluator.evaluate(fraction), mOut)
            } else rgbToColor(mOut)
        }
    }

    protected abstract class AbsHSVAnimator protected constructor(
        private val mAlphaEvaluator: AlphaEvaluator?,
        @ColorInt fromColor: Int,
        @ColorInt toColor: Int,
        @param:Size(3) private val mFrom: FloatArray,
        @param:Size(3) private val mTo: FloatArray
    ) : CCFAnimator(fromColor, toColor) {

        private val mOut: FloatArray = FloatArray(3)

        override fun getColor(fraction: Float): Int {
            mOut[0] = getHue(fraction)
            mOut[1] = mFrom[1] + (mTo[1] - mFrom[1]) * fraction
            mOut[2] = mFrom[2] + (mTo[2] - mFrom[2]) * fraction
            return if (mAlphaEvaluator != null) {
                Color.HSVToColor(mAlphaEvaluator.evaluate(fraction), mOut)
            } else Color.HSVToColor(mOut)
        }

        protected abstract fun getHue(fraction: Float): Float

    }

    protected class HSVAnimator(
        alphaEvaluator: AlphaEvaluator?,
        fromColor: Int,
        toColor: Int,
        fromHSV: FloatArray,
        toHSV: FloatArray
    ) : AbsHSVAnimator(alphaEvaluator, fromColor, toColor, fromHSV, toHSV) {

        private val mFromH: Float = fromHSV[0]
        private val mDiff: Float = toHSV[0] - fromHSV[0]

        override fun getHue(fraction: Float): Float {
            return mFromH + mDiff * fraction
        }
    }

    protected class HSVBackwardsAnimator(
        alphaEvaluator: AlphaEvaluator?,
        fromColor: Int,
        toColor: Int,
        fromHSV: FloatArray,
        toHSV: FloatArray
    ) : AbsHSVAnimator(alphaEvaluator, fromColor, toColor, fromHSV, toHSV) {

        private val mFromH: Float = fromHSV[0]
        private val mDiff: Float = 360f - abs(toHSV[0] - fromHSV[0])
        private val mFromIsBigger: Boolean

        init {
            mFromIsBigger = mFromH.compareTo(toHSV[0]) > 0
        }

        override fun getHue(fraction: Float): Float {
            val evaluated = mDiff * fraction
            if (mFromIsBigger) {
                val left = mFromH + evaluated
                return if (left.compareTo(360f) > 0) {
                    left - 360f
                } else left
            }
            val left = mFromH - evaluated
            return if (left.compareTo(.0f) < 0) {
                360f - abs(left)
            } else left
        }

    }

    protected class ConcatAnimator(private val mAnimators: Array<CCFAnimator?>) : CCFAnimator(0, 0) {

        private val mLength: Int = mAnimators.size
        private val mFractionStep: Float

        init {
            mFractionStep = 1f / mLength
        }

        override fun getColor(fraction: Float): Int {
            var index: Int
            var stepFraction: Float
            run {
                val i = (fraction / mFractionStep).toInt()
                if (i >= mLength) {
                    index = mLength - 1
                    stepFraction = 1f
                } else {
                    index = i
                    stepFraction = fraction % mFractionStep / mFractionStep
                }
            }
            return mAnimators[index]?.getColor(stepFraction) ?: Color.WHITE
        }

    }

    companion object {

        /**
         * Concats specified array of [CCFAnimator] into a [CCFAnimator.ConcatAnimator]. Maybe be used to
         * mix hsv, arg, argb CCFAnimators
         *
         * @see .hsv
         * @see .rgb
         * @see .argb
         * @param animators array of [CCFAnimator] to include in returned [CCFAnimator.ConcatAnimator]
         * @return [CCFAnimator]
         */
        fun concat(animators: Array<CCFAnimator?>): CCFAnimator {
            return ConcatAnimator(animators)
        }

        /**
         * Creates a [CCFAnimator] animate alpha of specified color
         *
         * @see .argb
         * @param color starting color
         * @param toAlpha alpha amount to animate specified color
         * @return [CCFAnimator]
         */
        fun alpha(
            @ColorInt color: Int,
            @ColorInt toAlpha: Int
        ): CCFAnimator {
            return argb(color, applyAlpha(color, toAlpha))
        }

        /**
         * Creates a [CCFAnimator] to animate `fromColor` color to `toColor`. Alpha property will be ignored
         *
         * @see .argb
         * @see .rgb
         * @param fromColor starting color
         * @param toColor end color
         * @return [CCFAnimator]
         */
        fun rgb(
            @ColorInt fromColor: Int,
            @ColorInt toColor: Int
        ): CCFAnimator {
            return RGBAnimator(null, fromColor, toColor)
        }

        /**
         * Creates a [CCFAnimator] to animate between array of colors.
         * For each pair of colors RGB CCFAnimator will be created
         *
         * @see .rgb
         * @param colors to animate
         * @return [CCFAnimator]
         */
        fun rgb(@Size(min = 2) colors: IntArray): CCFAnimator {
            val animators = arrayOfNulls<CCFAnimator>(colors.size - 1)
            var i = 0
            val length = animators.size
            while (i < length) {
                animators[i] = rgb(colors[i], colors[i + 1])
                i++
            }
            return concat(animators)
        }

        /**
         * Creates a [CCFAnimator] to animate `fromColor` color to `toColor`
         *
         * @see .rgb
         * @see .argb
         * @param fromColor starting color
         * @param toColor end color
         * @return [CCFAnimator]
         */
        fun argb(
            @ColorInt fromColor: Int,
            @ColorInt toColor: Int
        ): CCFAnimator {
            val fromAlpha = extractAlpha(fromColor)
            val toAlpha = extractAlpha(toColor)
            val alphaEvaluator: AlphaEvaluator?
            alphaEvaluator = if (fromAlpha != toAlpha) {
                AlphaEvaluatorImpl(fromAlpha, toAlpha)
            } else {
                null
            }
            return RGBAnimator(alphaEvaluator, fromColor, toColor)
        }

        /**
         * Constructs a [CCFAnimator] from specified array of colors
         *
         * @see .argb
         * @param colors colors to cross-fade (minimum length is 2)
         * @return [CCFAnimator]
         */
        fun argb(@Size(min = 2) colors: IntArray): CCFAnimator {
            val animators = arrayOfNulls<CCFAnimator>(colors.size - 1)
            var i = 0
            val length = animators.size
            while (i < length) {
                animators[i] = argb(colors[i], colors[i + 1])
                i++
            }
            return concat(animators)
        }

        /**
         * Creates a [CCFAnimator] to animate HSV of specified colors
         *
         * @see .hsv
         * @see .hsv
         * @param fromColor starting color
         * @param toColor end color
         * @param fromAlpha start alpha
         * @param toAlpha end alpha
         * @return [CCFAnimator]
         */
        @JvmOverloads
        fun hsv(
            @ColorInt fromColor: Int,
            @ColorInt toColor: Int,
            @IntRange(from = 0, to = 255) fromAlpha: Int = 0,
            @IntRange(from = 0, to = 255) toAlpha: Int = 0
        ): CCFAnimator {
            val alphaEvaluator: AlphaEvaluator? = if (fromAlpha != toAlpha) {
                AlphaEvaluatorImpl(fromAlpha, toAlpha)
            } else {
                null
            }
            val from = buildHSV(fromColor)
            val to = buildHSV(toColor)
            // determine whether we are backwards
            return if (isHSVBackwards(from[0], to[0])) {
                HSVBackwardsAnimator(alphaEvaluator, fromColor, toColor, from, to)
            } else
                HSVAnimator(alphaEvaluator, fromColor, toColor, from, to)
        }

        /**
         * Constructs a [CCFAnimator] from specified array of colors
         *
         * @see .hsv
         * @see .hsv
         * @param colors colors to animate
         * @return [CCFAnimator]
         */
        fun hsv(@Size(min = 2) colors: IntArray): CCFAnimator {
            val animators = arrayOfNulls<CCFAnimator>(colors.size - 1)
            var i = 0
            val length = animators.size
            while (i < length) {
                animators[i] = hsv(colors[i], colors[i + 1])
                i++
            }
            return concat(animators)
        }


        protected fun isHSVBackwards(fromH: Float, toH: Float): Boolean {
            return abs(toH - fromH) > 180f
        }

        protected fun buildHSV(@ColorInt color: Int): FloatArray {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            return hsv
        }

        protected fun buildRGB(@ColorInt color: Int): IntArray {
            val rgb = IntArray(3)
            rgb[0] = color shr 16 and 0xFF
            rgb[1] = color shr 8 and 0xFF
            rgb[2] = color and 0xFF
            return rgb
        }

        @ColorInt
        protected fun rgbToColor(rgb: IntArray): Int {
            return 0xFF shl 24 or (rgb[0] shl 16) or (rgb[1] shl 8) or rgb[2]
        }

        @ColorInt
        protected fun argbToColor(alpha: Int, rgb: IntArray): Int {
            return alpha shl 24 or (rgb[0] shl 16) or (rgb[1] shl 8) or rgb[2]
        }

        @ColorInt
        protected fun applyAlpha(color: Int, alpha: Int): Int {
            return color and 0x00FFFFFF or (alpha shl 24)
        }

        protected fun extractAlpha(@ColorInt color: Int): Int {
            return color ushr 24
        }

    }

}