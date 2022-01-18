package ir.am3n.needtool.views.recyclerview

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class RclVH<T>(itemView: View) : RecyclerView.ViewHolder(itemView), BaseVH<T> {

    val ctx: Context? get() = itemView.context
    val context: Context? get() = ctx

}
