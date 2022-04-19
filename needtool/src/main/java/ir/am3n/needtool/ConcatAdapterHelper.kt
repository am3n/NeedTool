package ir.am3n.needtool

import androidx.recyclerview.widget.ConcatAdapter


fun ConcatAdapter.clear() {
    while (adapters.size > 0) {
        removeAdapter(adapters[0])
        notifyItemRemoved(0)
    }
}

