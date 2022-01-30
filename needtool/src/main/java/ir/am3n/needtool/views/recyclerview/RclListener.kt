package ir.am3n.needtool.views.recyclerview

import android.view.View

interface RclListener<T> {

    fun onClick(position: Int, item: T, clickedView: View? = null)

    fun onLongClick(position: Int, item: T, clickedView: View? = null): Boolean

}

