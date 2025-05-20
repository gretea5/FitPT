package com.ssafy.presentation.util

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

    fun formatMeasureCreatedAt(createdAt: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val date = LocalDate.parse(createdAt, inputFormatter)
        return date.format(outputFormatter)
    }

    fun formatDateTime(input: String): String? {
        return try {
            val dateTime = LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.n]"))
            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
            dateTime.format(formatter)
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
            null
        }
    }
}