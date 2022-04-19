package ir.am3n.needtool

import android.view.View
import android.widget.ScrollView


fun ScrollView.focusOn(view: View) {
    val vTop = view.top
    val vBottom = view.bottom
    val sHeight = bottom
    smoothScrollTo((vTop + vBottom - sHeight) / 2, 0)
}
