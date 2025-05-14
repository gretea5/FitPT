package com.ssafy.presentation.util

object TimeUtils {
    fun parseDateTime(dateTimeString: String): Pair<String, String> {
        // "T"를 기준으로 날짜와 시간 분리
        val parts = dateTimeString.split("T")
        val date = parts[0]  // "2025-05-17"
        val time = parts[1].substring(0, 5)  // "17:30" (초 부분 제거)

        return Pair(date, time)
    }
}