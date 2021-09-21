package ir.am3n.needtool.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat
import ir.am3n.needtool.R
import ir.am3n.needtool.asStateList
import ir.am3n.needtool.isDark
import kotlinx.android.synthetic.main.layout_a3verticalseekvar.view.*
import kotlin.math.roundToInt

/**
 * A nicer, redesigned and vertical SeekBar
 */
@SuppressLint("ClickableViewAccessibility")
open class A3VerticalSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_MIN_VALUE = 0
        private const val DEFAULT_MAX_VALUE = 100
        private const val DEFAULT_PROGRESS = 50
    }

    private val onProgressChange: (Int) -> Unit = { newValue ->
        onProgressChangeListener?.invoke(newValue)
    }
    private val onPress: (Int) -> Unit = { progress ->
        onPressListener?.invoke(progress)
    }
    private val onRelease: (Int) -> Unit = { progress ->
        onReleaseListener?.invoke(progress)
    }

    private var initEnded = false
    private var touchListening = false

    var progressOnTouch = true
        set(value) {
            field = value
            applyAttributes()
        }

    var barCornerRadius: Int = 0
        set(value) {
            field = value
            applyAttributes()
        }
    var barBackgroundDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var barBackgroundStartColor: Int? = null
        set(value) {
            field = value
            barBackgroundDrawable = null
            applyAttributes()
        }
    var barBackgroundEndColor: Int? = null
        set(value) {
            field = value
            barBackgroundDrawable = null
            applyAttributes()
        }

    var barProgressDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var barProgressStartColor: Int? = null
        set(value) {
            field = value
            barProgressDrawable = null
            applyAttributes()
        }
    var barProgressEndColor: Int? = null
        set(value) {
            field = value
            barProgressDrawable = null
            applyAttributes()
        }

    var barStrokeWidth: Int = 0
        set(value) {
            field = value
            applyAttributes()
        }
    var barStrokeColor: Int = 0
        set(value) {
            field = value
            applyAttributes()
        }

    var barWidth: Int? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var minLayoutWidth: Int = 0
        set(value) {
            field = value
            applyAttributes()
        }
    var minLayoutHeight: Int = 0
        set(value) {
            field = value
            applyAttributes()
        }

    var maxPlaceholderDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var maxPlaceholderText: String? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var maxPlaceholderTextColor: Int? = null
        set(value) {
            field = value
            applyAttributes()
        }

    var midPlaceholderDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var midPlaceholderText: String? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var midPlaceholderTextColor: Int? = null
        set(value) {
            field = value
            applyAttributes()
        }

    var minPlaceholderDrawable: Drawable? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var minPlaceholderText: String? = null
        set(value) {
            field = value
            applyAttributes()
        }
    var minPlaceholderTextColor: Int? = null
        set(value) {
            field = value
            applyAttributes()
        }

    var minValue = DEFAULT_MIN_VALUE
        set(value) {
            val newValue = when {
                value < 0 -> 0
                else -> value
            }
            if (progress < newValue) progress = newValue
            field = newValue
            updateViews()
        }
    var maxValue = DEFAULT_MAX_VALUE
        set(value) {
            val newValue = when {
                value < 1 -> 1
                else -> value
            }
            if (progress > newValue) progress = newValue
            field = newValue
            updateViews()
        }

    var progress: Int = DEFAULT_PROGRESS
        set(value) {
            val newValue = when {
                value < minValue -> minValue
                value > maxValue -> maxValue
                else -> value
            }
            if (field != newValue) {
                onProgressChange.invoke(newValue)
            }
            field = newValue
            updateViews()
        }

    var onProgressChangeListener: ((Int) -> Unit)? = null
    var onPressListener: ((Int) -> Unit)? = null
    var onReleaseListener: ((Int) -> Unit)? = null

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        inflate(context, R.layout.layout_a3verticalseekvar, this)

        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.A3VerticalSeekBar, 0, 0)
            try {
                progressOnTouch = attributes.getBoolean(
                    R.styleable.A3VerticalSeekBar_a3vsb_progress_on_touch,
                    progressOnTouch
                )
                barCornerRadius = attributes.getLayoutDimension(
                    R.styleable.A3VerticalSeekBar_a3vsb_bar_corner_radius,
                    barCornerRadius
                )

                try {
                    barBackgroundDrawable = attributes.getDrawableOrThrow(R.styleable.A3VerticalSeekBar_a3vsb_bar_background)
                } catch (t: Throwable) {
                }
                try {
                    barBackgroundStartColor = attributes.getColorOrThrow(R.styleable.A3VerticalSeekBar_a3vsb_bar_background_gradient_start)
                } catch (t: Throwable) {
                }
                try {
                    barBackgroundEndColor = attributes.getColorOrThrow(R.styleable.A3VerticalSeekBar_a3vsb_bar_background_gradient_end)
                } catch (t: Throwable) {
                }

                try {
                    barProgressDrawable = attributes.getDrawableOrThrow(R.styleable.A3VerticalSeekBar_a3vsb_bar_progress)
                } catch (t: Throwable) {
                }
                try {
                    barProgressStartColor = attributes.getColorOrThrow(R.styleable.A3VerticalSeekBar_a3vsb_bar_progress_gradient_start)
                } catch (t: Throwable) {
                }
                try {
                    barProgressEndColor = attributes.getColorOrThrow(R.styleable.A3VerticalSeekBar_a3vsb_bar_progress_gradient_end)
                } catch (t: Throwable) {
                }

                barStrokeWidth = attributes.getDimensionPixelSize(R.styleable.A3VerticalSeekBar_a3vsb_bar_stroke_width, 0)
                barStrokeColor = attributes.getColor(R.styleable.A3VerticalSeekBar_a3vsb_bar_stroke_color, Color.WHITE)

                barWidth = attributes.getDimensionPixelSize(R.styleable.A3VerticalSeekBar_a3vsb_bar_width, barWidth ?: container.layoutParams.width)
                attributes.getLayoutDimension(R.styleable.A3VerticalSeekBar_android_layout_width, minLayoutWidth).also {
                    container.layoutParams.width = if (it != -1 && it < minLayoutWidth) minLayoutWidth else it
                }
                attributes.getLayoutDimension(R.styleable.A3VerticalSeekBar_android_layout_height, minLayoutHeight).also {
                    container.layoutParams.height = if (it != -1 && it < minLayoutHeight) minLayoutHeight else it
                }

                attributes.getDrawable(R.styleable.A3VerticalSeekBar_a3vsb_max_placeholder_src).also {
                    maxPlaceholderDrawable = it
                }
                attributes.getString(R.styleable.A3VerticalSeekBar_a3vsb_max_placeholder_text).also {
                    maxPlaceholderText = it
                }
                attributes.getColor(R.styleable.A3VerticalSeekBar_a3vsb_max_placeholder_text_color, Color.WHITE).also {
                    maxPlaceholderTextColor = it
                }

                attributes.getDrawable(R.styleable.A3VerticalSeekBar_a3vsb_mid_placeholder_src).also {
                    midPlaceholderDrawable = it
                }
                attributes.getString(R.styleable.A3VerticalSeekBar_a3vsb_mid_placeholder_text).also {
                    midPlaceholderText = it
                }
                attributes.getColor(R.styleable.A3VerticalSeekBar_a3vsb_mid_placeholder_text_color, Color.WHITE).also {
                    midPlaceholderTextColor = it
                }

                attributes.getDrawable(R.styleable.A3VerticalSeekBar_a3vsb_min_placeholder_src).also {
                    minPlaceholderDrawable = it
                }
                attributes.getString(R.styleable.A3VerticalSeekBar_a3vsb_min_placeholder_text).also {
                    minPlaceholderText = it
                }
                attributes.getColor(R.styleable.A3VerticalSeekBar_a3vsb_min_placeholder_text_color, Color.WHITE).also {
                    minPlaceholderTextColor = it
                }

                attributes.getInt(R.styleable.A3VerticalSeekBar_a3vsb_min_value, minValue).also {
                    minValue = it
                }
                attributes.getInt(R.styleable.A3VerticalSeekBar_a3vsb_max_value, maxValue).also {
                    maxValue = it
                }
                attributes.getInt(R.styleable.A3VerticalSeekBar_a3vsb_progress, progress).also {
                    progress = it
                }

            } finally {
                attributes.recycle()
            }
        }

        initEnded = true
        applyAttributes()
    }

    private fun applyAttributes() {

        if (initEnded) {
            initEnded = false

            barCardView.layoutParams.width = barWidth ?: 0
            barCardView.strokeWidth = barStrokeWidth
            barCardView.strokeColor = barStrokeColor
            barCardView.radius = barCornerRadius.toFloat()

            if (barBackgroundDrawable == null) {
                barBackgroundDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(barBackgroundStartColor!!, barBackgroundEndColor!!)
                ).apply { cornerRadius = 0f }
            }
            ViewCompat.setBackground(barBackground, barBackgroundDrawable)

            if (barProgressDrawable == null) {
                barProgressDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(barProgressStartColor!!, barProgressEndColor!!)
                ).apply { cornerRadius = 0f }
            }
            ViewCompat.setBackground(barProgress, barProgressDrawable)

            imgMaxPlaceholder.setImageDrawable(maxPlaceholderDrawable)
            txtMaxPlaceholder.text = maxPlaceholderText
            txtMaxPlaceholder.setTextColor(maxPlaceholderTextColor?:Color.WHITE)

            imgMidPlaceholder.setImageDrawable(midPlaceholderDrawable)
            txtMidPlaceholder.text = midPlaceholderText
            txtMidPlaceholder.setTextColor(midPlaceholderTextColor?:Color.WHITE)

            imgMinPlaceholder.setImageDrawable(minPlaceholderDrawable)
            txtMinPlaceholder.text = minPlaceholderText
            txtMinPlaceholder.setTextColor(minPlaceholderTextColor?:Color.WHITE)

            val maxPlaceholderLayoutParams = (imgMaxPlaceholder.layoutParams as LayoutParams)
            val maxPlaceholderHalfHeight = (imgMaxPlaceholder.drawable?.intrinsicHeight ?: 0) / 2
            maxPlaceholderLayoutParams.topMargin = maxPlaceholderHalfHeight
            maxPlaceholderLayoutParams.bottomMargin = maxPlaceholderLayoutParams.topMargin
            imgMaxPlaceholder.layoutParams = maxPlaceholderLayoutParams

            val minPlaceholderLayoutParams = (imgMinPlaceholder.layoutParams as LayoutParams)
            val minPlaceholderHalfHeight = (imgMinPlaceholder.drawable?.intrinsicHeight ?: 0) / 2
            minPlaceholderLayoutParams.bottomMargin = minPlaceholderHalfHeight
            minPlaceholderLayoutParams.topMargin = maxPlaceholderLayoutParams.bottomMargin
            imgMinPlaceholder.layoutParams = minPlaceholderLayoutParams


            if (progressOnTouch && !touchListening) {
                val action: (View, Int) -> Unit = { bar, positionY ->
                    val fillHeight = bar.measuredHeight
                    when {
                        positionY in 1 until fillHeight -> {
                            val newValue = maxValue - (positionY * maxValue / fillHeight)
                            progress = newValue
                        }
                        positionY <= 0 -> progress = maxValue
                        positionY >= fillHeight -> progress = minValue
                    }
                }
                barCardView.setOnTouchListener { bar, event ->
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_DOWN -> {
                            bar.parent.requestDisallowInterceptTouchEvent(true)
                            action.invoke(bar, event.y.roundToInt())
                            onPress.invoke(progress)
                        }
                        MotionEvent.ACTION_MOVE -> {
                            action.invoke(bar, event.y.roundToInt())
                        }
                        MotionEvent.ACTION_UP -> {
                            bar.parent.requestDisallowInterceptTouchEvent(false)
                            onRelease.invoke(progress)
                        }
                    }
                    true
                }
                touchListening = true
            } else if (!progressOnTouch) {
                barCardView.setOnTouchListener(null)
                touchListening = false
            }

            imgMidPlaceholder?.post {
                val imgIsDark = midPlaceholderDrawable?.toBitmap(200, 200)?.isDark == true && ImageViewCompat.getImageTintList(imgMidPlaceholder) == null
                val bgIsDark = (if (progress < maxValue / 2) barBackgroundDrawable else barProgressDrawable)?.toBitmap(100, 500)?.isDark == true
                if (imgIsDark && bgIsDark) {
                    ImageViewCompat.setImageTintList(imgMidPlaceholder, Color.parseColor("#eeeeee").asStateList)
                } else if (!imgIsDark && !bgIsDark) {
                    ImageViewCompat.setImageTintList(imgMidPlaceholder, Color.parseColor("#333333").asStateList)
                } else if (ImageViewCompat.getImageTintList(imgMidPlaceholder) != null) {
                    ImageViewCompat.setImageTintList(imgMidPlaceholder, null)
                }
            }

            initEnded = true

            updateViews()

        }
    }

    private fun updateViews() {
        if (initEnded) {
            post {
                barProgress.translationY = (barBackground.height * (maxValue - progress) / maxValue).toFloat()
                invalidate()
            }
        }
    }

}
