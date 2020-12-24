package ir.am3n.needtool.platekeyboard.keyboard.layouts

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import ir.am3n.needtool.platekeyboard.keyboard.KeyboardListener
import ir.am3n.needtool.platekeyboard.keyboard.controllers.KeyboardController
import ir.am3n.needtool.platekeyboard.utilities.ComponentUtils.getScreenWidth

class AlphabetKeyboardLayout(context: Context, controller: KeyboardController?) : KeyboardLayout(context, controller) {

    constructor(context: Context): this(context, null)

    private var columnWidth = 0.09f

    init {
        controller?.registerListener(object: KeyboardListener {
            override fun characterClicked(c: Char) {}
            override fun specialKeyClicked(key: KeyboardController.SpecialKey) {}
        })
    }

    override fun createRows(): List<LinearLayout> {
        return createLowerCaseRows()
    }

    private fun createLowerCaseRows(): List<LinearLayout> {

        val rowOne = ArrayList<View>()
        rowOne.add(createButton("ض", columnWidth, 'ض'))
        rowOne.add(createButton("ص", columnWidth, 'ص'))
        rowOne.add(createButton("ث", columnWidth, 'ث'))
        rowOne.add(createButton("ق", columnWidth, 'ق'))
        rowOne.add(createButton("ف", columnWidth, 'ف'))
        rowOne.add(createButton("غ", columnWidth, 'غ'))
        rowOne.add(createButton("ع", columnWidth, 'ع'))
        rowOne.add(createButton("ه", columnWidth, 'ه'))
        rowOne.add(createButton("خ", columnWidth, 'خ'))
        rowOne.add(createButton("ح", columnWidth, 'ح'))
        rowOne.add(createButton("ج", columnWidth, 'ج'))

        val rowTow = ArrayList<View>()
        rowTow.add(createButton("ش", columnWidth, 'ش'))
        rowTow.add(createButton("س", columnWidth, 'س'))
        rowTow.add(createButton("ی", columnWidth, 'ی'))
        rowTow.add(createButton("ب", columnWidth, 'ب'))
        rowTow.add(createButton("ل", columnWidth, 'ل'))
        rowTow.add(createButton("ا", columnWidth, 'ا'))
        rowTow.add(createButton("ت", columnWidth, 'ت'))
        rowTow.add(createButton("ن", columnWidth, 'ن'))
        rowTow.add(createButton("م", columnWidth, 'م'))
        rowTow.add(createButton("ک", columnWidth, 'ک'))
        rowTow.add(createButton("گ", columnWidth, 'گ'))

        val rowThree = ArrayList<View>()
        rowThree.add(createButton("ظ", columnWidth, 'ظ'))
        rowThree.add(createButton("ط", columnWidth, 'ط'))
        rowThree.add(createButton("ژ", columnWidth, 'ژ'))
        rowThree.add(createButton("ز", columnWidth, 'ز'))
        rowThree.add(createButton("ر", columnWidth, 'ر'))
        rowThree.add(createButton("ذ", columnWidth, 'ذ'))
        rowThree.add(createButton("د", columnWidth, 'د'))
        rowThree.add(createButton("پ", columnWidth, 'پ'))
        rowThree.add(createButton("و", columnWidth, 'و'))
        rowThree.add(createButton("چ", columnWidth, 'چ'))
        rowThree.add(createButton("⌫", columnWidth, KeyboardController.SpecialKey.BACKSPACE))

        val rowFour = ArrayList<View>()
        if (hasNextFocus) {
            rowFour.add(createButton("بعدی", (columnWidth * 3f), KeyboardController.SpecialKey.NEXT))
        } else {
            rowFour.add(createButton("تائید", (columnWidth * 3f), KeyboardController.SpecialKey.DONE))
        }

        val rows = ArrayList<LinearLayout>()
        rows.add(createRow(rowOne))
        rows.add(createRow(rowTow))
        rows.add(createRow(rowThree))
        rows.add(createRow(rowFour))

        return rows
    }

}