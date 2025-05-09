package com.ssafy.presentation.util

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat

object CommonUtils {

    fun changeTextColor(
        context: Context,
        fullText: String,
        changeText: String,
        color: Int
    ): SpannableString {
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf(changeText)
        val endIndex = startIndex + changeText.length

        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    fun changeMultipleTextColors(
        context: Context,
        fullText: String,
        changes: List<Pair<String, Int>>
    ): SpannableString {
        val spannableString = SpannableString(fullText)

        for ((changeText, colorResId) in changes) {
            var startIndex = fullText.indexOf(changeText)
            while (startIndex >= 0) {
                val endIndex = startIndex + changeText.length

                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, colorResId)),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                startIndex = fullText.indexOf(changeText, endIndex)
            }
        }

        return spannableString
    }

}