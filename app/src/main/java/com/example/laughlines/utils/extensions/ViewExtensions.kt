package com.example.laughlines.utils.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

internal fun View.hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
    this.clearFocus()
}

//internal fun View.showKeyboard() {
//    val methodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    methodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
//}

internal fun View.show() {
    visibility = View.VISIBLE
}

internal fun View.hide() {
    visibility = View.GONE
}