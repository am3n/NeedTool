package ir.am3n.needtool

import android.view.inputmethod.EditorInfo
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