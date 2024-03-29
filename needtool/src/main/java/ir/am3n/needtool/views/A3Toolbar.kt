package ir.am3n.needtool.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.*
import androidx.core.widget.ImageViewCompat
import ir.am3n.needtool.*

class A3Toolbar : RelativeLayout {

    open class Menu internal constructor(
        @IdRes val id: Int,
        val title: String,
        val iconDrawable: Drawable?,
        @DrawableRes val iconResource: Int?,
        val iconTint: ColorStateList? = null,
        val iconPadding: Int = 0
    ) {
        companion object {
            fun create(
                @IdRes id: Int,
                title: String,
                iconDrawable: Drawable?,
                iconTint: ColorStateList? = null,
                iconPadding: Int = 0
            ): Menu {
                return Menu(id, title, iconDrawable, null, iconTint, iconPadding)
            }

            fun create(
                @IdRes id: Int,
                title: String,
                @DrawableRes iconResource: Int?,
                iconTint: ColorStateList? = null,
                iconPadding: Int = 0
            ): Menu {
                return Menu(id, title, null, iconResource, iconTint, iconPadding)
            }
        }
    }

    class ToolbarMenu(
        id: Int,
        title: String,
        iconDrawable: Drawable?,
        iconResource: Int?,
        iconTint: ColorStateList? = null,
        iconPadding: Int = 0,
        val view: AppCompatImageButton
    ) : Menu(id, title, iconDrawable, iconResource, iconTint, iconPadding)


    private var initialized = true

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

    private var optionsMenuTint: Int? = null
    private var menu: MutableList<Menu> = mutableListOf()
    private var menuBtns: MutableList<AppCompatImageButton> = mutableListOf()

    private var onItemClick: ((index: Int, id: Int?, view: View?) -> Unit)? = null

    var direction: Int? = null
        set(value) {
            field = value
            requestLayout()
        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, defStyleAttr)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun onFinishInflate() {
        refresh()
        super.onFinishInflate()
    }

    override fun onAttachedToWindow() {
        refresh()
        super.onAttachedToWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        refresh()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("RestrictedApi")
    private fun init(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {

        if (context == null) return
        initialized = false

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

        var optionsMenu: Int? = null
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
            val menuBuilder = MenuBuilder(context)
            MenuInflater(context).inflate(optionsMenu, menuBuilder)
            for (i in 0 until menuBuilder.size) {
                menu.add(
                    Menu(
                        menuBuilder[i].itemId,
                        menuBuilder[i].title.toString(),
                        menuBuilder[i].icon,
                        null,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            menuBuilder[i].iconTintList else null,
                    )
                )
                menuBtns.add(AppCompatImageButton(context).apply { id = menu[i].id })
                addView(menuBtns.last())
            }
        }

        initialized = true

    }

    fun refresh() {

        if (layoutParams?.height == null) return

        val isRtl = when (direction) {
            0 -> false
            1 -> true
            2 -> parent.layoutDirection == 1
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
            removeRule(ALIGN_PARENT_START)
            removeRule(ALIGN_PARENT_END)
            removeRule(ALIGN_PARENT_LEFT)
            removeRule(ALIGN_PARENT_RIGHT)
            addRule(if (isRtl) ALIGN_PARENT_RIGHT else ALIGN_PARENT_LEFT, 1)
            if (isRtl) {
                updateMargins(right = 4.iDp2Px, left = 0)
            } else {
                updateMargins(left = 4.iDp2Px, right = 0)
            }
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
            removeRule(START_OF)
            removeRule(END_OF)
            removeRule(LEFT_OF)
            removeRule(RIGHT_OF)
            addRule(if (isRtl) LEFT_OF else RIGHT_OF, imgbBack!!.id)
        }
        txtTitle?.gravity = (if (isRtl) Gravity.RIGHT else Gravity.LEFT) or Gravity.CENTER_VERTICAL
        txtTitle?.isSingleLine = true
        txtTitle?.updatePadding(left = 4.iDp2Px, right = 4.iDp2Px)
        txtTitle?.text = txtTitleText
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                txtTitle?.setTextAppearance(txtTitleAppearance!!)
            } else {
                txtTitle?.setTextAppearance(context, txtTitleAppearance!!)
            }
        } catch (t: Throwable) {
        }
        if (txtTitleColor != null)
            txtTitle?.setTextColor(txtTitleColor!!)
        if (txtTitleSize != null) {
            txtTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtTitleSize!!)
        }

        initMenu()

    }

    private fun initMenu() {

        val isRtl = when (direction) {
            0 -> false
            1 -> true
            2 -> parent.layoutDirection == 1
            3 -> resources.isRtl
            else -> false
        }

        val defaultBackground = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, defaultBackground, true)

        menu.forEachIndexed { index, item ->
            menuBtns[index].updateLayoutParams<LayoutParams> {
                height = layoutParams.height
                width = layoutParams.height
                if (index == 0) {
                    removeRule(ALIGN_PARENT_START)
                    removeRule(ALIGN_PARENT_END)
                    removeRule(ALIGN_PARENT_LEFT)
                    removeRule(ALIGN_PARENT_RIGHT)
                    addRule(if (isRtl) ALIGN_PARENT_LEFT else ALIGN_PARENT_RIGHT, 1)
                    updateMargins(left = if (isRtl) 10.iDp2Px else 4.iDp2Px, right = if (isRtl) 4.iDp2Px else 10.iDp2Px)
                } else {
                    removeRule(START_OF)
                    removeRule(END_OF)
                    removeRule(LEFT_OF)
                    removeRule(RIGHT_OF)
                    addRule(if (isRtl) RIGHT_OF else LEFT_OF, menu[index-1].id)
                    updateMargins(left = 4.iDp2Px, right = 4.iDp2Px)
                }
            }
            if (item.iconDrawable != null)
                menuBtns[index].setImageDrawable(item.iconDrawable)
            if (item.iconResource != null)
                menuBtns[index].setImageResource(item.iconResource)
            if (optionsMenuTint != null)
                ImageViewCompat.setImageTintList(menuBtns[index], optionsMenuTint!!.asStateList)
            menuBtns[index].setBackgroundResource(defaultBackground.resourceId)
            menuBtns[index].setSafeOnClickListener {
                onItemClick?.invoke(index, item.id, menuBtns[index])
            }
            menuBtns[index].scaleType = ImageView.ScaleType.CENTER_INSIDE
            menuBtns[index].setPadding(item.iconPadding)
        }
    }

    //**************************************************************************************************

    fun setBackIcon(@DrawableRes iconResource: Int?) {
        if (iconResource != null) {
            imgbBackIcon = iconResource
            imgbBack?.setImageResource(imgbBackIcon!!)
        }
    }

    fun setBackIconTint(@ColorInt tint: Int?) {
        if (imgbBack != null && tint != null) {
            imgbBackTint = tint
            ImageViewCompat.setImageTintList(imgbBack!!, imgbBackTint!!.asStateList)
        }
    }

    fun setBackIconPadding(padding: Int) {
        imgbBackPadding = padding
        imgbBack?.setPadding(imgbBackPadding!!)
    }

    fun setTitle(title: String) {
        txtTitleText = title
        txtTitle?.text = title
    }

    fun onBackClick(onBackClick: () -> Unit) {
        imgbBack?.setOnClickListener { onBackClick.invoke() }
    }

    fun getMenu(): List<ToolbarMenu> {
        return menu.mapIndexed { index, menu ->
            ToolbarMenu(
                menu.id,
                menu.title,
                menu.iconDrawable,
                menu.iconResource,
                menu.iconTint,
                menu.iconPadding,
                menuBtns[index]
            )
        }
    }

    fun setMenu(menu: List<Menu>) {
        this.menu.clear()
        this.menu.addAll(menu)
        menuBtns.forEach { removeView(it) }
        menuBtns.clear()
        this.menu.forEach {
            menuBtns.add(AppCompatImageButton(context).apply { id = it.id })
            addView(menuBtns.last())
        }
        initMenu()
    }

    fun onItemClick(onItemClick: (index: Int, id: Int?, view: View?) -> Unit) {
        this.onItemClick = onItemClick
    }

}