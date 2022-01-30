package ir.am3n.needtool.views.recyclerview

import android.view.View
import kotlinx.android.extensions.LayoutContainer
import java.io.Serializable

interface BaseVH<T> : LayoutContainer, Serializable {

    override val containerView: View?

    val clickView: View?

    val longClickView: View?

    var isSelected: Boolean

    fun bind(item: T, position: Int)

    fun changed(item: T, position: Int, payloads: MutableList<Any>)

}
