package ir.am3n.needtool.platekeyboard.keyboard

import ir.am3n.needtool.platekeyboard.keyboard.controllers.KeyboardController

interface KeyboardListener {
    fun characterClicked(c: Char)
    fun specialKeyClicked(key: KeyboardController.SpecialKey)
}