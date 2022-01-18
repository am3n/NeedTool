package ir.am3n.needtool.views.recyclerview

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class RtlStaggeredLayoutManager : StaggeredGridLayoutManager {

    private var spanner: (() -> Int)? = null
    private var rtlize: (() -> Boolean)? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(spanCount: Int, orientation: Int
    ) : super(spanCount, orientation)

    constructor(
        spanCount: Int,
        orientation: Int,
        rtlize: () -> Boolean = { false },
        spanner: () -> Int = { spanCount }
    ) : super(spanCount, orientation) { this.rtlize = rtlize; this.spanner = spanner }

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        super.onMeasure(recycler, state, widthSpec, heightSpec)
        spanner?.invoke().let { count ->
            if (count != null) {
                spanCount = count
            }
        }
    }

    override fun getLayoutDirection(): Int {
        return when {
            rtlize == null -> super.getLayoutDirection()
            rtlize?.invoke() == true -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                View.LAYOUT_DIRECTION_RTL
            } else {
                0
            }
            else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                View.LAYOUT_DIRECTION_LTR
            } else {
                0
            }
        }
    }

}
