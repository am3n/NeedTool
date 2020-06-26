package ir.am3n.needtool

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatEditText
import java.lang.Exception
import java.text.NumberFormat

/*
    how to use:
    edtCard?.addTextChangedListener(SerialCardTextWatcher(edtCard))
 */

abstract class BaseTextWatcher : TextWatcher {

    var beforeText: String = ""

    abstract var edittext: AppCompatEditText?

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        beforeText = s.toString()
    }

    abstract fun format(@NonNull amount: String): String

    companion object {

        fun removeNonNumeric(@NonNull numberString: String): String {
            var numbers = ""
            for (i in numberString) {
                if (i.isDigit())
                    numbers += i
            }
            return numbers
        }

        fun getNewCursorPosition(digitCountToRightOfCursor: Int, numberString: String): Int {
            var position = 0
            var c = digitCountToRightOfCursor
            for (i in numberString.reversed()) {
                if (c == 0)
                    break
                if (i.isDigit())
                    c--
                position++
            }
            return numberString.length - position
        }

        fun getNumberOfDigits(@NonNull text: String): Int {
            var count = 0
            for (i in text)
                if (i.isDigit())
                    count++
            return count
        }

    }

}



class SerialCardTextWatcher(override var edittext: AppCompatEditText? = null) : BaseTextWatcher() {

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s == null) return
        // 1. get cursor position : p0 = start + before
        val initialCursorPosition = start + before
        //2. get digit count after cursor position : c0
        val numOfDigitsToRightOfCursor = getNumberOfDigits(
            beforeText.substring(initialCursorPosition, beforeText.length)
        )
        val newAmount = format(s.toString())
        edittext?.removeTextChangedListener(this)
        edittext?.setText(newAmount)
        //set new cursor position
        try {
            edittext?.setSelection(
                getNewCursorPosition(
                    numOfDigitsToRightOfCursor,
                    newAmount
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        edittext?.addTextChangedListener(this)
    }

    override fun format(amount: String): String {
        val result = removeNonNumeric(amount)
        val amt = if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result)) result else ""
        var res = ""
        for (i in amt.indices) {
            if (i != 0 && i % 4 == 0)
                res += " - "
            res += amt[i]
        }
        return res
    }

}



class CostTextWatcher(override var edittext: AppCompatEditText? = null) : BaseTextWatcher() {

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s == null) return
        // 1. get cursor position : p0 = start + before
        val initialCursorPosition = start + before
        //2. get digit count after cursor position : c0
        val numOfDigitsToRightOfCursor = getNumberOfDigits(
            beforeText.substring(initialCursorPosition, beforeText.length)
        )
        val newAmount = format(s.toString())
        edittext?.removeTextChangedListener(this)
        edittext?.setText(newAmount)
        //set new cursor position
        try {
            edittext?.setSelection(
                getNewCursorPosition(
                    numOfDigitsToRightOfCursor,
                    newAmount
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        edittext?.addTextChangedListener(this)
    }

    override fun format(amount: String): String {
        return formatCost(amount)
    }

    companion object {

        fun formatCost(amount: String): String {
            val result = removeNonNumeric(amount)
            var amt: Long? = if (result.isNotEmpty() && result.isNumeric()) result.toLongOrNull() else 0
            if (amt == null) {
                amt = result.substring(0, 15).toLong()
            }
            val formatter = NumberFormat.getNumberInstance()
            //return formatter.format(amt).plus(" ﷼")
            //return "﷼ ".plus(formatter.format(amt))
            return formatter.format(amt)
        }

    }

}



class PostalCodeTextWatcher(override var edittext: AppCompatEditText?) : BaseTextWatcher() {

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s == null) return
        // 1. get cursor position : p0 = start + before
        val initialCursorPosition = start + before
        //2. get digit count after cursor position : c0
        val numOfDigitsToRightOfCursor = getNumberOfDigits(
            beforeText.substring(initialCursorPosition, beforeText.length)
        )
        val newAmount = format(s.toString())
        edittext?.removeTextChangedListener(this)
        edittext?.setText(newAmount)
        //set new cursor position
        try {
            edittext?.setSelection(
                getNewCursorPosition(
                    numOfDigitsToRightOfCursor,
                    newAmount
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        edittext?.addTextChangedListener(this)
    }

    override fun format(amount: String): String {
        val result = removeNonNumeric(amount)
        val amt = if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result)) result else ""
        var res = ""
        for (i in amt.indices) {
            if (i != 0 && i % 5 == 0)
                res += "-"
            res += amt[i]
        }
        return res
    }

}