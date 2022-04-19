package ir.am3n.needtool

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

interface EdittextOnDone {
    fun callback(): Boolean
}

fun EditText.onDone(edittextOnDone: EdittextOnDone) {
    imeOptions = EditorInfo.IME_ACTION_DONE
    maxLines = 1
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            return@setOnEditorActionListener edittextOnDone.callback()
        }
        return@setOnEditorActionListener false
    }
}

fun EditText.onDone(edittextOnDone: () -> Boolean) {
    imeOptions = EditorInfo.IME_ACTION_DONE
    maxLines = 1
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            return@setOnEditorActionListener edittextOnDone()
        }
        return@setOnEditorActionListener false
    }
}


fun EditText.showSoftKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
        ?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}
