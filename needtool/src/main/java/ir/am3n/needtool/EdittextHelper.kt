package ir.am3n.needtool

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

interface EdittextOnAction {
    fun callback(): Boolean
}

fun EditText.onDone(edittextOnAction: EdittextOnAction) {
    imeOptions = EditorInfo.IME_ACTION_DONE
    maxLines = 1
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            return@setOnEditorActionListener edittextOnAction.callback()
        }
        return@setOnEditorActionListener false
    }
}

fun EditText.onDone(edittextOnAction: () -> Boolean) {
    imeOptions = EditorInfo.IME_ACTION_DONE
    maxLines = 1
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            return@setOnEditorActionListener edittextOnAction()
        }
        return@setOnEditorActionListener false
    }
}


fun EditText.onSearch(edittextOnAction: EdittextOnAction) {
    imeOptions = EditorInfo.IME_ACTION_SEARCH
    maxLines = 1
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            return@setOnEditorActionListener edittextOnAction.callback()
        }
        return@setOnEditorActionListener false
    }
}

fun EditText.onSearch(edittextOnAction: () -> Boolean) {
    imeOptions = EditorInfo.IME_ACTION_SEARCH
    maxLines = 1
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            return@setOnEditorActionListener edittextOnAction()
        }
        return@setOnEditorActionListener false
    }
}



fun EditText.showSoftKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
        ?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}
