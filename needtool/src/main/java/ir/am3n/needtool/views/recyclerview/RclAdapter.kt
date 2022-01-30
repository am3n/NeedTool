package ir.am3n.needtool.views.recyclerview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.am3n.needtool.setSafeOnClickListener

abstract class RclAdapter<T, VH: RclVH<T>> : RecyclerView.Adapter<VH>() {

    var list: MutableList<T> = ArrayList()

    val isSelectMode: Boolean get() = list.any { isSelected(it) }

    open var listener: RclListener<T>? = null

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
        holder.clickView?.setSafeOnClickListener {
            listener?.onClick(holder.adapterPosition, list[holder.adapterPosition], it)
        }
        holder.longClickView?.setOnLongClickListener {
            return@setOnLongClickListener listener?.onLongClick(holder.adapterPosition, list[holder.adapterPosition], it) ?: true
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