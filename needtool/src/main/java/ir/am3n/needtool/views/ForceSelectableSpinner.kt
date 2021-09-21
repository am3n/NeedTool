package ir.am3n.needtool.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView

import androidx.appcompat.widget.AppCompatSpinner

class ForceSelectableSpinner(
    context: Context,
    attrs: AttributeSet
) : AppCompatSpinner(context, attrs), AdapterView.OnItemSelectedListener {

    var onItemSelectedEvenIfUnchangedListener: OnItemSelectedListener? = null

    override fun setSelection(position: Int) {
        super.setSelection(position)
        if (onItemSelectedEvenIfUnchangedListener != null)
            onItemSelectedEvenIfUnchangedListener!!.onItemSelected(null, null, position, 0)
    }

    override fun setSelection(position: Int, animate: Boolean) {
        super.setSelection(position, animate)
        if (onItemSelectedEvenIfUnchangedListener != null)
            onItemSelectedEvenIfUnchangedListener!!.onItemSelected(null, null, position, 0)
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
        if (onItemSelectedEvenIfUnchangedListener != null)
            onItemSelectedEvenIfUnchangedListener!!.onItemSelected(adapterView, view, position, l)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {
        if (onItemSelectedEvenIfUnchangedListener != null)
            onItemSelectedEvenIfUnchangedListener!!.onNothingSelected(adapterView)
    }

}