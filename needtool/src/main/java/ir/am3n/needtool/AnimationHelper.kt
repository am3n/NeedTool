package ir.am3n.needtool

import android.view.animation.*
import android.widget.ImageView


fun ImageView.rotate(start: Boolean, clockwise: Boolean = true, interpolator: Interpolator? = AccelerateDecelerateInterpolator(), duration: Long = 1000) {
    this.clearAnimation()
    if (start) {
        val rotateAnimation = RotateAnimation(0f, if (clockwise) 360f else -360f,
            Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f)
        interpolator?.let { rotateAnimation.interpolator = it }
        rotateAnimation.fillAfter = true
        rotateAnimation.repeatCount = -1
        rotateAnimation.duration = duration
        this.startAnimation(rotateAnimation)
    }
}

fun ImageView.rotateHide(start: Boolean) {
    if (start) {
        val rotateAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f)
        rotateAnimation.interpolator = AccelerateDecelerateInterpolator()
        rotateAnimation.fillAfter = true
        rotateAnimation.repeatCount = -1
        rotateAnimation.duration = 1000

        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.interpolator = AccelerateInterpolator()
        alphaAnimation.fillAfter = true
        alphaAnimation.duration = 200
        alphaAnimation.startOffset = 100

        val animatorSet = AnimationSet(false)
        animatorSet.addAnimation(rotateAnimation)
        animatorSet.addAnimation(alphaAnimation)
        animatorSet.fillAfter = true

        this.clearAnimation()
        this.startAnimation(animatorSet)

    } else {
        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.interpolator = DecelerateInterpolator()
        alphaAnimation.fillAfter = true
        alphaAnimation.duration = 300

        this.clearAnimation()
        this.startAnimation(alphaAnimation)
    }
}

