package ir.am3n.needtool.views.recyclerview

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper

@SuppressLint("ClickableViewAccessibility")
abstract class RclMvVH<T>(
    itemView: View,
) : RclVH<T>(itemView), ItemTouchHelperViewHolder {
    var itemTouchHelper: ItemTouchHelper? = null
    fun initDargView(view: View) {
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                itemTouchHelper?.startDrag(this)
            }
            false
        }
    }
}