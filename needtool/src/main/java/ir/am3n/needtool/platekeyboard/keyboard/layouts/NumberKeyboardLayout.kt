package ir.am3n.needtool.platekeyboard.keyboard.layouts

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import ir.am3n.needtool.platekeyboard.keyboard.controllers.KeyboardController


class NumberKeyboardLayout(context: Context, controller: KeyboardController?) : KeyboardLayout(context, controller) {

    constructor(context: Context): this(context, null)

    override fun createRows(): List<LinearLayout> {
        val columnWidth = 0.20f
        textSize = 22.0f

        val rowOne = ArrayList<View>()
        rowOne.add(createButton("۱", columnWidth, '۱'))
        rowOne.add(createButton("۲", columnWidth, '۲'))
        rowOne.add(createButton("۳", columnWidth, '۳'))

        val rowTwo = ArrayList<View>()
        rowTwo.add(createButton("۴", columnWidth, '۴'))
        rowTwo.add(createButton("۵", columnWidth, '۵'))
        rowTwo.add(createButton("۶", columnWidth, '۶'))

        val rowThree = ArrayList<View>()
        rowThree.add(createButton("۷", columnWidth, '۷'))
        rowThree.add(createButton("۸", columnWidth, '۸'))
        rowThree.add(createButton("۹", columnWidth, '۹'))

        val rowFour = ArrayList<View>()
        rowFour.add(createButton("⌫", columnWidth, KeyboardController.SpecialKey.BACKSPACE))
        rowFour.add(createButton("۰", columnWidth, '۰'))
        if (hasNextFocus) {
            rowFour.add(createButton("بعدی", columnWidth, KeyboardController.SpecialKey.NEXT))
        } else {
            rowFour.add(createButton("تائید", columnWidth, KeyboardController.SpecialKey.DONE))
        }

        val rows = ArrayList<LinearLayout>()
        rows.add(createRow(rowOne))
        rows.add(createRow(rowTwo))
        rows.add(createRow(rowThree))
        rows.add(createRow(rowFour))

        return rows
    }
}
