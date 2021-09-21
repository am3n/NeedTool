package ir.am3n.needtool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.AnimRes
import androidx.appcompat.app.AppCompatActivity

abstract class AnimatedActivity(
    var animate: Boolean = true,
    var animationsDirection: Direction = Direction.LTR,
    @AnimRes private var enterAnimationRes: Int = R.anim.slide_in_right,
    @AnimRes private var exitAnimationRes: Int = R.anim.slide_out_left,
    @AnimRes private var popEnterAnimationRes: Int = R.anim.slide_in_left,
    @AnimRes private var popExitAnimationRes: Int = R.anim.slide_out_right
) : AppCompatActivity() {
    
    enum class Direction { LTR, RTL }

    abstract var tag: String

    val enterAnimation: Int get() {
        return when (animationsDirection) {
            Direction.LTR -> {
                if (resources.isRtl) popEnterAnimationRes
                else enterAnimationRes
            }
            Direction.RTL -> {
                if (resources.isRtl) enterAnimationRes
                else popEnterAnimationRes
            }
        }
    }

    val exitAnimation: Int get() {
        return when (animationsDirection) {
            Direction.LTR -> {
                if (resources.isRtl) popExitAnimationRes
                else exitAnimationRes
            }
            Direction.RTL -> {
                if (resources.isRtl) exitAnimationRes
                else popExitAnimationRes
            }
        }
    }

    val popEnterAnimation: Int get() {
        return when (animationsDirection) {
            Direction.LTR -> {
                if (resources.isRtl) enterAnimationRes
                else popEnterAnimationRes
            }
            Direction.RTL -> {
                if (resources.isRtl) popEnterAnimationRes
                else enterAnimationRes
            }
        }
    }

    val popExitAnimation: Int get() {
        return when (animationsDirection) {
            Direction.LTR -> {
                if (resources.isRtl) exitAnimationRes
                else popExitAnimationRes
            }
            Direction.RTL -> {
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

    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
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