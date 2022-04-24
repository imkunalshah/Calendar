package com.kunal.calendar.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.View.*
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun View.gone() {
    this.visibility = GONE
}

fun View.visible() {
    this.visibility = VISIBLE
}

fun View.inVisible() {
    this.visibility = INVISIBLE
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun Context.getDialogWidth(): Int {
    val displayMetrics = DisplayMetrics()
    (this as Activity?)?.windowManager
        ?.defaultDisplay
        ?.getMetrics(displayMetrics)
    val width = displayMetrics.widthPixels
    return (width * 80) / 100
}