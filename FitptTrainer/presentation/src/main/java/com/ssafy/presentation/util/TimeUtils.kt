package com.ssafy.presentation.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeUtils {
    fun parseDateTime(dateTimeString: String): Pair<String, String> {
            // "T"를 기준으로 날짜와 시간 분리
            val parts = dateTimeString.split("T")
            val date = parts[0]  // "2025-05-17"
            val time = parts[1].substring(0, 5)  // "17:30" (초 부분 제거)

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
}