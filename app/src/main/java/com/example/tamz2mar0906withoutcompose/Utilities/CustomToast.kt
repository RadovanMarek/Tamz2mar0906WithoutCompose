package com.example.tamz2mar0906withoutcompose.Utilities

import android.content.Context
import android.util.TypedValue
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun showCustomSnackbar(view: View, context: Context, message: String) {
    val primaryColor = resolveColorFromTheme(context, com.google.android.material.R.attr.colorPrimary)
    val onPrimaryColor = resolveColorFromTheme(context, com.google.android.material.R.attr.colorOnPrimary)

    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)

    snackbar.setBackgroundTint(primaryColor)
    snackbar.setTextColor(onPrimaryColor)

    snackbar.show()
}

fun resolveColorFromTheme(context: Context, attr: Int): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}
