package ir.am3n.needtool.views.recyclerview

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class RclMvAdapter<T, VH: RclMvVH<T>>(
    rcl: RecyclerView
) : RclAdapter<T, VH>(), ItemTouchHelperAdapter {

    private var mItemTouchHelper: ItemTouchHelper? = null

    var isDismissMode = false
    var isMoveMode = false

    init {
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(this@RclMvAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(rcl)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemTouchHelper = mItemTouchHelper
    }

    override fun onItemDismiss(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(list, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return isDismissMode
    }

    override fun isLongPressDragEnabled(): Boolean {
        return isMoveMode
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        mItemTouchHelper?.attachToRecyclerView(recyclerView)
    }

}