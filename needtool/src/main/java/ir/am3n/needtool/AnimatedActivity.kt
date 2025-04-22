package ir.am3n.needtool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.AnimRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import ir.am3n.needtool.AnimatedActivity.Direction.*

abstract class AnimatedActivity(
    var animate: Boolean = true,
    var animationsDirection: Direction = LTR,
    @AnimRes private var enterAnimationRes: Int = R.anim.slide_in_right,
    @AnimRes private var exitAnimationRes: Int = R.anim.slide_out_left,
    @AnimRes private var popEnterAnimationRes: Int = R.anim.slide_in_left,
    @AnimRes private var popExitAnimationRes: Int = R.anim.slide_out_right
) : AppCompatActivity() {

    enum class Direction { LTR, RTL }

    abstract var tag: String

    val enterAnimation: Int get() {
        return when (animationsDirection) {
            LTR -> {
                if (resources.isRtl) popEnterAnimationRes
                else enterAnimationRes
            }
            RTL -> {
                if (resources.isRtl) enterAnimationRes
                else popEnterAnimationRes
            }
        }
    }

    val exitAnimation: Int get() {
        return when (animationsDirection) {
            LTR -> {
                if (resources.isRtl) popExitAnimationRes
                else exitAnimationRes
            }
            RTL -> {
                if (resources.isRtl) exitAnimationRes
                else popExitAnimationRes
            }
        }
    }

    val popEnterAnimation: Int get() {
        return when (animationsDirection) {
            LTR -> {
                if (resources.isRtl) enterAnimationRes
                else popEnterAnimationRes
            }
            RTL -> {
                if (resources.isRtl) popEnterAnimationRes
                else enterAnimationRes
            }
        }
    }

    val popExitAnimation: Int get() {
        return when (animationsDirection) {
            LTR -> {
                if (resources.isRtl) exitAnimationRes
                else popExitAnimationRes
            }
            RTL -> {
                if (resources.isRtl) popExitAnimationRes
                else exitAnimationRes
            }
        }
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        super.startActivity(intent, options)
        Log.d("Me-AnimatedActivity", "$tag | startActivity()")
        enter()
    }

    @Deprecated("Deprecated in Java")
    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        Log.d("Me-AnimatedActivity", "$tag | startActivityForResult()")
        enter()
    }

    override fun finish() {
        super.finish()
        Log.d("Me-AnimatedActivity", "$tag | finish()")
        exit()
    }
    
    private fun enter() {
        if (animate) {
            overridePendingTransition(enterAnimation, exitAnimation)
        }
    }
    
    private fun exit() {
        if (animate) {
            overridePendingTransition(popEnterAnimation, popExitAnimation)
        }
    }

}


abstract class AnimatedActivityWithLayoutRes(
    @LayoutRes var layoutRes: Int,
    var animate: Boolean = true,
    var animationsDirection: AnimatedActivity.Direction = LTR,
    @AnimRes private var enterAnimationRes: Int = R.anim.slide_in_right,
    @AnimRes private var exitAnimationRes: Int = R.anim.slide_out_left,
    @AnimRes private var popEnterAnimationRes: Int = R.anim.slide_in_left,
    @AnimRes private var popExitAnimationRes: Int = R.anim.slide_out_right
): AppCompatActivity(layoutRes) {

    abstract var tag: String

    val enterAnimation: Int get() {
        return when (animationsDirection) {
            LTR -> {
                if (resources.isRtl) popEnterAnimationRes
                else enterAnimationRes
            }
            RTL -> {
                if (resources.isRtl) enterAnimationRes
                else popEnterAnimationRes
            }
        }
    }

    val exitAnimation: Int get() {
        return when (animationsDirection) {
            LTR -> {
                if (resources.isRtl) popExitAnimationRes
                else exitAnimationRes
            }
            RTL -> {
                if (resources.isRtl) exitAnimationRes
                else popExitAnimationRes
            }
        }
    }

    val popEnterAnimation: Int get() {
        return when (animationsDirection) {
            LTR -> {
                if (resources.isRtl) enterAnimationRes
                else popEnterAnimationRes
            }
            RTL -> {
                if (resources.isRtl) popEnterAnimationRes
                else enterAnimationRes
            }
        }
    }

    val popExitAnimation: Int get() {
        return when (animationsDirection) {
            LTR -> {
                if (resources.isRtl) exitAnimationRes
                else popExitAnimationRes
            }
            RTL -> {
                if (resources.isRtl) popExitAnimationRes
                else exitAnimationRes
            }
        }
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        super.startActivity(intent, options)
        Log.d("Me-AnimatedActivity", "$tag | startActivity()")
        enter()
    }

    @Deprecated("Deprecated in Java")
    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        Log.d("Me-AnimatedActivity", "$tag | startActivityForResult()")
        enter()
    }

    override fun finish() {
        super.finish()
        Log.d("Me-AnimatedActivity", "$tag | finish()")
        exit()
    }

    private fun enter() {
        if (animate) {
            overridePendingTransition(enterAnimation, exitAnimation)
        }
    }

    private fun exit() {
        if (animate) {
            overridePendingTransition(popEnterAnimation, popExitAnimation)
        }
    }

}
