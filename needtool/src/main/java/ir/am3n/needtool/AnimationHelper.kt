package ir.am3n.needtool

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.*
import androidx.core.view.isVisible


fun Context.areSystemAnimationsEnabled(): Boolean {
    val duration: Float
    val transition: Float
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        duration = Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        transition = Settings.Global.getFloat(contentResolver, Settings.Global.TRANSITION_ANIMATION_SCALE, 1f)
    } else {
        duration = Settings.System.getFloat(contentResolver, Settings.System.ANIMATOR_DURATION_SCALE, 1f)
        transition = Settings.System.getFloat(contentResolver, Settings.System.TRANSITION_ANIMATION_SCALE, 1f)
    }
    return duration != 0f && transition != 0f
}

//-----------------------------------------------------

fun View.rotate(start: Boolean = true, clockwise: Boolean = true, interpolator: Interpolator? = LinearInterpolator(), duration: Long = 1000) {
    this.clearAnimation()
    if (start) {
        val rotateAnimation = RotateAnimation(
            0f, if (clockwise) 360f else -360f,
            Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f
        )
        interpolator?.let { rotateAnimation.interpolator = it }
        rotateAnimation.fillAfter = true
        rotateAnimation.repeatMode = Animation.RESTART
        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.duration = duration
        this.startAnimation(rotateAnimation)
    }
}

fun View.rotateHide(start: Boolean = true) {
    if (start) {
        val rotateAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f)
        rotateAnimation.interpolator = AccelerateDecelerateInterpolator()
        rotateAnimation.fillAfter = true
        rotateAnimation.repeatCount = Animation.INFINITE
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

//-----------------------------------------------------

fun View.blink(
    start: Boolean = true,
    from: Float = 1f,
    to: Float = 0f,
    interpolator: Interpolator? = AccelerateDecelerateInterpolator(),
    duration: Long = 2000,
    times: Int = 1
) {
    this.clearAnimation()
    if (start) {
        val animation = AlphaAnimation(from, to)
        interpolator?.let { animation.interpolator = it }
        animation.repeatMode = Animation.REVERSE
        animation.repeatCount = Animation.INFINITE
        animation.duration = duration * times
        this.startAnimation(animation)
    }
}

//-----------------------------------------------------

fun View.hide(gone: Boolean = true, disable: Boolean = true) {
    if (!isVisible)
        return
    clearAnimation()
    val alpha = AlphaAnimation(1f, 0f).apply {
        interpolator = AccelerateInterpolator()
        duration = 100
        fillAfter = true
    }
    val scale = ScaleAnimation(
        1f, .7f,
        1f, .7f,
        Animation.RELATIVE_TO_SELF, .5f,
        Animation.RELATIVE_TO_SELF, .5f
    ).apply {
        interpolator = AccelerateInterpolator()
        duration = 100
        fillAfter = true
    }
    val set = AnimationSet(false)
    set.addAnimation(scale)
    set.addAnimation(alpha)
    set.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            if (disable)
                isEnabled = false
            if (gone) {
                isVisible = false
                if (tag == "hided but visiblity flag is true")
                    tag = ""
            } else {
                if (tag == null || tag !is String || tag == "") {
                    tag = "hided but visiblity flag is true"
                } else
                    Log.e("AnimationHelper", "error", Exception("tag needed & you used it"))
            }
        }
        override fun onAnimationRepeat(animation: Animation?) {}
    })
    set.fillAfter = true
    startAnimation(set)
}

fun View.show(enable: Boolean = true, onEnd: () -> Unit = {}) {
    if (isVisible && tag != "hided but visiblity flag is true")
        return
    isVisible = true
    if (enable)
        isEnabled = true
    clearAnimation()
    val alpha = AlphaAnimation(0f, 1f).apply {
        interpolator = AccelerateInterpolator()
        duration = 100
        startOffset = 0
    }
    val scale = ScaleAnimation(
        .7f, 1f,
        .7f, 1f,
        Animation.RELATIVE_TO_SELF, .5f,
        Animation.RELATIVE_TO_SELF, .5f
    ).apply {
        duration = 100
        interpolator = AccelerateInterpolator()
    }
    val set = AnimationSet(false)
    set.addAnimation(scale)
    set.addAnimation(alpha)
    set.fillAfter = true
    set.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            if (tag == "hided but visiblity flag is true") {
                tag = ""
            }
            onEnd()
        }
        override fun onAnimationRepeat(animation: Animation?) {}
    })
    startAnimation(set)
    /*if (!clickable) {
        isClickable = true
        isFocusable = true
    }*/
}

//-----------------------------------------------------

fun View.breath() {

    clearAnimation()

    var scale1: Animation? = null

    val scale2 = ScaleAnimation(
        1.2f, .8f,
        1.2f, .8f,
        Animation.RELATIVE_TO_SELF, .5f,
        Animation.RELATIVE_TO_SELF, .5f
    ).apply {
        startOffset = 700
        duration = 2000
        interpolator = AccelerateDecelerateInterpolator()
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                startAnimation(scale1)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    scale1 = ScaleAnimation(
        .8f, 1.2f,
        .8f, 1.2f,
        Animation.RELATIVE_TO_SELF, .5f,
        Animation.RELATIVE_TO_SELF, .5f
    ).apply {
        startOffset = 300
        duration = 1800
        interpolator = DecelerateInterpolator()
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                startAnimation(scale2)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    startAnimation(scale1)

    /*val set = AnimationSet(false)
    set.addAnimation(scale1)
    set.addAnimation(scale2)
    set.fillAfter = true
    set.repeatCount = ValueAnimator.INFINITE
    set.repeatMode = ValueAnimator.REVERSE
    startAnimation(set)*/

    /*val set = AnimatorSet()
    set.playSequentially(
        ObjectAnimator.ofFloat(this, "scaleX", 0.3f, 0.5f, 0.9f, 0.8f, 0.9f, 1f),
        ObjectAnimator.ofFloat(this, "scaleY", 0.3f, 0.5f, 0.9f, 0.8f, 0.9f, 1f)
    )
    set.duration = 5000
    set.start()*/

}