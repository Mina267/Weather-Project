package com.example.weather.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.weather.R
import com.google.android.material.snackbar.Snackbar

object ShowSnackbar {

    @JvmStatic
    fun customSnackbar(
        context: Context,
        view: View,
        message: String,
        actionText: String,
        iconResId: Int,
        action: (View) -> Unit
    ) {
        // Create Snackbar with dynamic message and action
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(" $actionText") { action(view) } // Dynamic action text and lambda as action

        // Get the Snackbar's view
        val snackbarView = snackbar.view

        // Set background color and rounded corners dynamically
        snackbarView.background = ContextCompat.getDrawable(context, R.drawable.snackbar_background)

        // Add margins to elevate the Snackbar above the navigation bar
        if (snackbarView.layoutParams is FrameLayout.LayoutParams) {
            val params = snackbarView.layoutParams as FrameLayout.LayoutParams
            params.setMargins(16, 0, 20, 140) // Adjust bottom margin as needed
            snackbarView.layoutParams = params
        }

        // Customize the main message text color to be weak (lighter color)
        val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
        val textColor = ContextCompat.getColor(context, R.color.blue_grey_50)
        textView.setTextColor(textColor)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Adjust text size as needed

        // Set a dynamic icon for the Snackbar with the same color as the text
        if (iconResId != 0) {
            var icon: Drawable? = ContextCompat.getDrawable(context, iconResId)
            if (icon != null) {
                icon = DrawableCompat.wrap(icon) // Wrap drawable to allow tinting
                DrawableCompat.setTint(icon, textColor) // Set icon color to match text color
                textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
            }
        }

        // Customize the action button text to emphasize (stronger and more noticeable)
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.accent_color)) // Emphasize action button text color
        val actionTextView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_action)
        actionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Adjust action button text size
        actionTextView.setTypeface(null, android.graphics.Typeface.BOLD) // Bold font for emphasis

        // Adjust padding for a more compact look
        snackbarView.setPadding(32, 1, 32, 1) // Smaller padding for height reduction

        // Show the Snackbar
        snackbar.show()
    }
}
