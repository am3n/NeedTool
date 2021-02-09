package ir.am3n.needtool

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.extensions.LayoutContainer
import java.io.Serializable
import kotlin.math.roundToInt


abstract class RclAdapter<T, VH: RclVH<T>> : RecyclerView.Adapter<VH>() {

    var list: MutableList<T> = ArrayList()

    val isSelectMode: Boolean get() = list.any { isSelected(it) }

    open var listener: RclListener? = null

    open val animate = false
    private var onAttach = true
    private var duration = 50L

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list[position], position)
        holder.clickView?.setSafeOnClickListener { listener?.onClick(holder.adapterPosition, it) }
        holder.longClickView?.setOnLongClickListener {
            return@setOnLongClickListener listener?.onLongClick(holder.adapterPosition, it) ?: true
        }
        if (animate)
            animate(holder.itemView, position)
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.changed(list[position], position, payloads)
        }
    }

    private fun animate(itemView: View, position: Int) {
        var i = position
        if (!onAttach) i = -1
        val isNotFirstItem = i == -1
        i++
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 0.5f, 1.0f)
        ObjectAnimator.ofFloat(itemView, "alpha", 0f).start()
        animator.startDelay = if (isNotFirstItem) duration / 2 else i * duration / 3
        animator.duration = 300
        animatorSet.play(animator)
        animator.start()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                onAttach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
    }

    open fun isSelected(item: T): Boolean {
        return false
    }

    //------------------------------------------------

    fun get(position: Int): T {
        return list[position]
    }

    fun notifyAllItemsChanged(payloads: Any) {
        notifyItemRangeChanged(0, itemCount, payloads)
    }

    open fun clear() {
        val size = list.size
        list.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun set(newList: List<T>?) {
        if (newList != null) {
            clear()
            list.addAll(newList)
            notifyItemRangeInserted(0, list.size)
        }
    }

    fun set(newList: ArrayList<T>?) {
        set(newList?.toList())
    }

    fun set(newList: Array<T>?) {
        set(newList?.asList())
    }

    fun add(item: T?) {
        item?.let {
            list.add(it)
            notifyItemInserted(list.size - 1)
        }
    }

    fun add(_position: Int?, item: T) {
        _position?.let {
            var position = it
            if (position > itemCount)
                position = itemCount
            if (position < 0)
                position = 0
            list.add(position, item)
            notifyItemInserted(position)
        }
    }

    fun add(newList: List<T>?) {
        if (newList != null) {
            val start = list.size
            list.addAll(newList)
            notifyItemRangeInserted(start, newList.size)
        }
    }

    fun add(_position: Int, newList: List<T>) {
        var position = _position
        if (position > itemCount)
            position = itemCount
        if (position < 0)
            position = 0
        list.addAll(position, newList)
        notifyItemInserted(position)
    }

    fun change(position: Int, item: T, what: Int) {
        /* need copy impl
        val oldItem = list[position]
        val newItem = oldItem.copy(item)
        list[position] = newItem
        _notifyItemChanged(position, what)*/
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition)
            return

        val movingItem = list[fromPosition]
        removeAt(fromPosition)

        if (fromPosition < toPosition) {
            add(toPosition - 1, movingItem)
        } else {
            add(toPosition, movingItem)
        }

        /* removeAt() and add() have notify()
        notifyItemMoved(fromPosition, toPosition)*/
    }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItemRange(positionStart: Int, itemCount: Int) {
        repeat(itemCount) {
            removeAt(positionStart)
        }
        notifyItemRangeRemoved(positionStart, itemCount)
    }

    fun autoNotify(new: List<T>, compare: (T, T) -> Boolean) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = list.size
            override fun getNewListSize() = new.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compare(list[oldItemPosition], new[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list[oldItemPosition] == new[newItemPosition]
            }
        }, true).apply {
            list.clear()
            list.addAll(new)
            dispatchUpdatesTo(this@RclAdapter)
        }
    }

}

/*abstract class SecRclAdapter<T, VH: SecRclVH<T>> : RecyclerView.Adapter<VH>() {

}

annotation class BaseAdapter<>*/


//************************************************************************************************


abstract class RclVH<T>(itemView: View)
    : RecyclerView.ViewHolder(itemView), BaseVH<T>

/*abstract class SecRclVH<T>(itemView: View)
    : SectioningAdapter.ItemViewHolder(itemView), BaseVH<T>*/

interface BaseVH<T> : LayoutContainer, Serializable {

    var isSelected: Boolean

    override val containerView: View?

    val clickView: View?

    val longClickView: View?

    fun bind(item: T, position: Int)

    fun changed(item: T, position: Int, payloads: MutableList<Any>)

}


//************************************************************************************************


interface RclListener {

    fun onClick(itemPosition: Int, clickedView: View? = null)

    fun onLongClick(itemPosition: Int, clickedView: View? = null): Boolean

}


//************************************************************************************************


/**
 * MiddleDividerItemDecoration is a [RecyclerView.ItemDecoration] that can be used as a divider
 * between items of a [LinearLayoutManager]. It supports both [.HORIZONTAL] and
 * [.VERTICAL] orientations.
 * It can also supports [.ALL], included both the horizontal and vertical. Mainly used for GridLayout.
 * <pre>
 * For normal usage with LinearLayout,
 * val mItemDecoration = MiddleDividerItemDecoration(context!!,DividerItemDecoration.VERTICAL)
 * For GridLayoutManager with inner decorations,
 * val mItemDecoration = MiddleDividerItemDecoration(context!!,MiddleDividerItemDecoration.ALL)
 * recyclerView.addItemDecoration(mItemDecoration);
</pre> *
 */
/**
 * Creates a divider [RecyclerView.ItemDecoration] that can be used with a
 * [LinearLayoutManager].
 *
 * @param context Current context, it will be used to access resources.
 * @param orientation Divider orientation. Should be [.HORIZONTAL] or [.VERTICAL].
 */
class MiddleDividerItemDecoration(context: Context, orientation: Int
) : RecyclerView.ItemDecoration() {

    private var mDivider: Drawable? = null

    /**
     * Current orientation. Either [.HORIZONTAL] or [.VERTICAL].
     */
    private var mOrientation: Int = 0

    private val mBounds = Rect()

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this " + "DividerItemDecoration. Please set that attribute all call setDrawable()")
        }
        a.recycle()
        setOrientation(orientation)
    }

    fun setDividerColor(color: Int) {
        mDivider?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    /**
     * Sets the orientation for this divider. This should be called if
     * [RecyclerView.LayoutManager] changes orientation.
     *
     * @param orientation [.HORIZONTAL] or [.VERTICAL]
     */
    fun setOrientation(orientation: Int) {
        require(!(orientation != HORIZONTAL && orientation != VERTICAL && orientation != ALL)) {
            "Invalid orientation. It should be either HORIZONTAL or VERTICAL"
        }
        mOrientation = orientation
    }

    /**
     * Sets the [Drawable] for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    fun setDrawable(drawable: Drawable) {
        if (drawable == null) {
            throw IllegalArgumentException("Drawable cannot be null.")
        }
        mDivider = drawable
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || mDivider == null) {
            return
        }

        when (mOrientation) {
            ALL -> {
                drawVertical(c, parent)
                drawHorizontal(c, parent)
            }
            VERTICAL -> drawVertical(c, parent)
            else -> drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }

        var childCount = parent.childCount
        if (parent.layoutManager is RtlGridLayoutManager) {
            var leftItems = childCount % (parent.layoutManager as RtlGridLayoutManager).spanCount
            if (leftItems == 0) {
                leftItems = (parent.layoutManager as RtlGridLayoutManager).spanCount
            }
            //Identify last row, and don't draw border for these items
            childCount -= leftItems
        }

        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i) ?: return
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + Math.round(child.translationY)
            val top = bottom - mDivider?.intrinsicHeight!!
            mDivider?.setBounds(left, top, right, bottom)
            mDivider?.draw(canvas)
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int

        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }

        var childCount = parent.childCount
        if (parent.layoutManager is RtlGridLayoutManager) {
            childCount = (parent.layoutManager as RtlGridLayoutManager).spanCount
        }

        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i) ?: return
            parent.layoutManager?.getDecoratedBoundsWithMargins(child, mBounds)
            val right =
                (if (parent.layoutManager is RtlGridLayoutManager) mBounds.left else mBounds.right) + child.translationX.roundToInt()
            val left = right - mDivider!!.intrinsicWidth
            mDivider?.setBounds(left, top, right, bottom)
            mDivider?.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0)
            return
        }
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
        } else {
            outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
        }
    }

    companion object {
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL

        //mainly used for GridLayoutManager
        const val ALL = 2

        private const val TAG = "DividerItem"
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

}


//*************************************************************************************************


class RtlGridLayoutManager : GridLayoutManager {

    private var spanner: (() -> Int)? = null
    private var rtlize: (() -> Boolean)? = null

    constructor(context: Context, spanCount: Int)
            : super(context, spanCount)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean)
            : super(context, spanCount, orientation, reverseLayout)

    constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean, rtlize: () -> Boolean, spanner: () -> Int)
            : super(context, spanCount, orientation, reverseLayout) { this.rtlize = rtlize; this.spanner = spanner }

    override fun isLayoutRTL(): Boolean {
        return rtlize?.invoke() ?: super.isLayoutRTL()
    }

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        super.onMeasure(recycler, state, widthSpec, heightSpec)
        spanner?.invoke().let { count ->
            if (count != null) {
                spanCount = count
            }
        }
    }

}



class RtlStaggeredLayoutManager : StaggeredGridLayoutManager {

    private var spanner: (() -> Int)? = null
    private var rtlize: (() -> Boolean)? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(spanCount: Int, orientation: Int
    ) : super(spanCount, orientation)

    constructor(spanCount: Int, orientation: Int, rtlize: () -> Boolean, spanner: () -> Int
    ) : super(spanCount, orientation) { this.rtlize = rtlize; this.spanner = spanner }

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        super.onMeasure(recycler, state, widthSpec, heightSpec)
        spanner?.invoke().let { count ->
            if (count != null) {
                spanCount = count
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getLayoutDirection(): Int {
        return if (rtlize == null) super.getLayoutDirection()
        else if (rtlize?.invoke() == true) View.LAYOUT_DIRECTION_RTL
        else View.LAYOUT_DIRECTION_LTR
    }

}