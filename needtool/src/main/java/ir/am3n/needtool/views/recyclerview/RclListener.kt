package ir.am3n.needtool.views.recyclerview

import android.view.View

interface RclListener {

    fun onClick(itemPosition: Int, clickedView: View? = null)

    fun onLongClick(itemPosition: Int, clickedView: View? = null): Boolean

}

