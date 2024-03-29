package ir.am3n.needtool.views.recyclerview

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class RclVH<T>(itemView: View) : RecyclerView.ViewHolder(itemView), BaseVH<T> {

    val context: Context? get() = itemView.context

}
