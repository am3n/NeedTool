package ir.am3n.needtool.views.recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

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


