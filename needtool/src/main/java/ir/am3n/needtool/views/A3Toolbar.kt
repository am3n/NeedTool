package ir.am3n.needtool.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.*
import androidx.core.widget.ImageViewCompat
import ir.am3n.needtool.*

class A3Toolbar : RelativeLayout {

    private var direction: Int? = null

    private var imgbBack: AppCompatImageButton? = null
    private var imgbBackIcon: Int? = null
    private var imgbBackTint: Int? = null
    private var imgbBackPadding: Int? = null
    private var imgbBackScaleType: Int? = null
    private var imgbBackBackground: Int? = null

    private var txtTitle: AppCompatTextView? = null
    private var txtTitleText: String = ""
    private var txtTitleColor: Int? = null
    private var txtTitleSize: Float? = null
    private var txtTitleAppearance: Int? = null

    private var menu: MenuBuilder? = null
    private var optionsMenu: Int? = null
    private var optionsMenuTint: Int? = null
    private var items: MutableList<AppCompatImageButton> = mutableListOf()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        refresh()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refresh()
    }

    @SuppressLint("RestrictedApi")
    private fun init(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {

        if (context == null) return

        val ta: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.A3Toolbar, defStyleAttr, 0)

        if (ta.hasValue(R.styleable.A3Toolbar_a3_direction))
            direction = ta.getInt(R.styleable.A3Toolbar_a3_direction, 0)

        if (ta.hasValue(R.styleable.A3Toolbar_a3_backIcon))
            imgbBackIcon = ta.getResourceId(R.styleable.A3Toolbar_a3_backIcon, 0)
        if (ta.hasValue(R.styleable.A3Toolbar_a3_backTint))
            imgbBackTint = ta.getColor(R.styleable.A3Toolbar_a3_backTint, Color.WHITE)
        if (ta.hasValue(R.styleable.A3Toolbar_a3_backPadding))
            imgbBackPadding = ta.getDimensionPixelSize(R.styleable.A3Toolbar_a3_backPadding, 0)
        if (ta.hasValue(R.styleable.A3Toolbar_a3_backScaleType))
            imgbBackScaleType = ta.getInt(R.styleable.A3Toolbar_a3_backScaleType, -1)
        if (ta.hasValue(R.styleable.A3Toolbar_a3_backBackground))
            imgbBackBackground = ta.getResourceId(R.styleable.A3Toolbar_a3_backBackground, 0)

        if (ta.hasValue(R.styleable.A3Toolbar_a3_titleText))
            txtTitleText = ta.getString(R.styleable.A3Toolbar_a3_titleText) ?: ""
        if (ta.hasValue(R.styleable.A3Toolbar_a3_titleColor))
            txtTitleColor = ta.getColor(R.styleable.A3Toolbar_a3_titleColor, Color.WHITE)
        if (ta.hasValue(R.styleable.A3Toolbar_a3_titleSize))
            txtTitleSize = ta.getDimensionPixelSize(R.styleable.A3Toolbar_a3_titleSize, 16.iDp2Px).toFloat()
        if (ta.hasValue(R.styleable.A3Toolbar_a3_titleAppearance))
            txtTitleAppearance = ta.getResourceId(R.styleable.A3Toolbar_a3_titleAppearance, 0)

        if (ta.hasValue(R.styleable.A3Toolbar_a3_optionsMenu))
            optionsMenu = ta.getResourceId(R.styleable.A3Toolbar_a3_optionsMenu, 0)
        if (ta.hasValue(R.styleable.A3Toolbar_a3_optionsMenuIconTint))
            optionsMenuTint = ta.getColor(R.styleable.A3Toolbar_a3_optionsMenuIconTint, Color.WHITE)

        ta.recycle()

        imgbBack = AppCompatImageButton(context)
        addView(imgbBack)

        txtTitle = AppCompatTextView(context)
        addView(txtTitle)

        if (optionsMenu != null) {
            menu = MenuBuilder(context)
            MenuInflater(context).inflate(optionsMenu!!, menu)
            for (i in 0 until menu!!.size) {
                items.add(AppCompatImageButton(context))
                addView(items.last())
            }
        }

    }

    private fun refresh() {

        if (layoutParams?.height == null) return

        val isRtl = when (direction) {
            0 -> false
            1 -> true
            2 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) parent.layoutDirection==1 else false
            3 -> resources.isRtl
            else -> false
        }

        val defaultBackground = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, defaultBackground, true)

        if (imgbBack?.id == null || imgbBack?.id == NO_ID)
            imgbBack?.id = ViewCompat.generateViewId()
        imgbBack?.updateLayoutParams<LayoutParams> {
            height = layoutParams.height
            width = layoutParams.height
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                removeRule(if (isRtl) ALIGN_PARENT_LEFT else ALIGN_PARENT_RIGHT)
            } else {
                addRule(if (isRtl) ALIGN_PARENT_LEFT else ALIGN_PARENT_RIGHT, 0)
            }
            addRule(if (isRtl) ALIGN_PARENT_RIGHT else ALIGN_PARENT_LEFT, 1)
            if (isRtl)
                updateMargins(right = 4.iDp2Px)
            else
                updateMargins(left = 4.iDp2Px)
        }
        if (imgbBackPadding != null && imgbBackPadding!! > 0)
            imgbBack?.setPadding(imgbBackPadding!!)
        if (imgbBackIcon != null)
            imgbBack?.setImageResource(imgbBackIcon!!)
        else
            imgbBack?.isVisible = false
        if (imgbBackScaleType != null && imgbBackScaleType!! >= 0)
            imgbBack?.scaleType = ImageView.ScaleType.values()[imgbBackScaleType!!]
        if (imgbBackTint != null)
            ImageViewCompat.setImageTintList(imgbBack!!, imgbBackTint!!.asStateList)
        if (imgbBackBackground != null) {
            imgbBack?.setBackgroundResource(imgbBackBackground!!)
        } else {
            imgbBack?.setBackgroundResource(defaultBackground.resourceId)
        }
        imgbBack?.scaleX = if (isRtl) -1f else 1f



        txtTitle?.updateLayoutParams<LayoutParams> {
            width = LayoutParams.MATCH_PARENT
            height = layoutParams.height

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                removeRule(if (isRtl) RIGHT_OF else LEFT_OF)
            } else {
                addRule(if (isRtl) RIGHT_OF else LEFT_OF, 0)
            }
            addRule(if (isRtl) LEFT_OF else RIGHT_OF, imgbBack!!.id)
            gravity = (if (isRtl) Gravity.RIGHT else Gravity.LEFT) or Gravity.CENTER_VERTICAL

        }
        txtTitle?.isSingleLine = true
        txtTitle?.updatePadding(left = 4.iDp2Px, right = 4.iDp2Px)
        txtTitle?.gravity = (if (isRtl) Gravity.RIGHT else Gravity.LEFT) or Gravity.CENTER_VERTICAL
        txtTitle?.text = txtTitleText
        if (txtTitleColor != null)
            txtTitle?.setTextColor(txtTitleColor!!)
        if (txtTitleSize != null) {
            txtTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtTitleSize!!)
        }
        try { txtTitle?.setTextAppearance(context, txtTitleAppearance!!) } catch (t: Throwable) {}


        items.forEachIndexed { index, imgb ->
            imgb.updateLayoutParams<LayoutParams> {
                height = layoutParams.height
                width = layoutParams.height
                if (index == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        removeRule(if (isRtl) ALIGN_PARENT_RIGHT else ALIGN_PARENT_LEFT)
                    } else {
                        addRule(if (isRtl) ALIGN_PARENT_RIGHT else ALIGN_PARENT_LEFT, 0)
                    }
                    addRule(if (isRtl) ALIGN_PARENT_LEFT else ALIGN_PARENT_RIGHT, 1)
                    updateMargins(left = if (isRtl) 10.iDp2Px else 4.iDp2Px, right = if (isRtl) 4.iDp2Px else 10.iDp2Px)
                } else {
                    addRule(if (isRtl) LEFT_OF else RIGHT_OF)
                    addRule(if (isRtl) RIGHT_OF else LEFT_OF, items[index - 1].id)
                    updateMargins(left = 4.iDp2Px, right = 4.iDp2Px)
                }
            }
            imgb.setImageDrawable(menu?.get(index)?.icon)
            if (optionsMenuTint != null)
                ImageViewCompat.setImageTintList(imgb, optionsMenuTint!!.asStateList)
            imgb.setBackgroundResource(defaultBackground.resourceId)
        }

    }

    //**************************************************************************************************

    fun onBackClick(onBackClick: () -> Unit) {
        imgbBack?.setOnClickListener { onBackClick.invoke() }
    }

    fun onItemClick(onItemClick: (index: Int, id: Int?, view: View?) -> Unit) {
        items.forEachIndexed { index, button ->
            button.setOnClickListener {
                onItemClick(index, menu?.get(index)?.itemId, button)
            }
        }
    }

}