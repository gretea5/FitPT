package com.ssafy.presentation.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtils {
    fun parseDateTime(dateTimeString: String): Pair<String, String> {
            val parts = dateTimeString.split("T")
            val date = parts[0]
            val time = parts[1].substring(0, 5)

            return Pair(date, time)
        }

    fun calculateScheduleTimes(selectedDate: LocalDate, selectedTime: String): Pair<String, String> {
        val startTime = "${selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}T${selectedTime}:00"

        val endHour = selectedTime.split(":")[0].toInt() + 1
        val endTime = "${selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}T${endHour.toString().padStart(2, '0')}:00:00"

        return Pair(startTime, endTime)
    }

    fun addOneHour(timeStr: String): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val time = LocalTime.parse(timeStr, formatter)

        val newTime = time.plusHours(1)

        return newTime.format(formatter)
    }

    fun formatDateTime(dateTimeString: String): String {
        try {
            val dateTime = LocalDateTime.parse(dateTimeString)
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh:mm")

            return dateTime.format(formatter)
        } catch (e: Exception) {
            return "날짜 형식 오류"
        }
    }

    fun formatDateToMonthDay(dateString: String): String {
        val dateTime = LocalDateTime.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("MM/dd")

        return dateTime.format(formatter)
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun parseDate(dateString: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }

    fun getYearFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }

    fun getMonthFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.MONTH) + 1
    }
}