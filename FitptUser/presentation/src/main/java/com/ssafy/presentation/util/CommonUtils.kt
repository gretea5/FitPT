package com.ssafy.presentation.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ssafy.presentation.databinding.ToastMultiLineCustomBinding
import com.ssafy.presentation.databinding.ToastSingleLineCustomBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CommonUtils {
    //천단위 콤마
    fun makeComma(num: Int): String {
        val comma = DecimalFormat("#,###")
        return comma.format(num)
    }

    fun makeCommaDecimal(num: BigDecimal): String {
        val comma = DecimalFormat("#,###")
        return comma.format(num)
    }

    fun getKoreanAge(dateString: String): String {
        val birthDate: LocalDate = try {
            val dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            dateTime.toLocalDate()
        } catch (e: DateTimeParseException) {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        }
        val birthYear = birthDate.year
        val currentYear = LocalDate.now().year
        val koreanAge = currentYear - birthYear + 1
        return "${koreanAge}세"
    }

    fun formatBirthToYYYYMMDD(birth: String): String {
        val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return try {
            val dateTime = LocalDateTime.parse(birth, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            dateTime.toLocalDate().format(outputFormatter)
        } catch (e: DateTimeParseException) {
            try {
                val date = LocalDate.parse(birth, DateTimeFormatter.ISO_LOCAL_DATE)
                date.format(outputFormatter)
            } catch (e2: DateTimeParseException) {
                "Invalid date format"
            }
        }
    }

    fun formatBirthDate(inputDate: String): String? {
        return try {
            if (inputDate.isEmpty()) return null
            val inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val date = LocalDate.parse(inputDate, inputFormatter)
            val dateTime = date.atTime(15, 0, 0)
            dateTime.format(outputFormatter)
        } catch (e: Exception) {
            null
        }
    }

    fun formatCreatedAt(isoDateTime: String): String {
        val parsedDateTime = parseFlexibleDateTime(isoDateTime)
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh:mm")
            .withLocale(Locale.KOREAN) // Korean locale for proper AM/PM display
        val localDateTime = LocalDateTime.parse(parsedDateTime, inputFormatter)
        return localDateTime.format(outputFormatter)
    }

    fun parseFlexibleDateTime(dateTimeStr: String): String {
        // 소수점 이하 자릿수 보정
        val fixedDateTimeStr = if (dateTimeStr.contains(".")) {
            val (main, fraction) = dateTimeStr.split(".")
            val normalizedFraction = fraction.padEnd(6, '0').take(6) // 항상 6자리로 맞춤
            "$main.$normalizedFraction"
        } else {
            dateTimeStr
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        return LocalDateTime.parse(fixedDateTimeStr, formatter).toString()
    }

    fun formatMeasureCreatedAt(createdAt: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val date = LocalDate.parse(createdAt, inputFormatter)
        return date.format(outputFormatter)
    }

    fun convertToCurrentYearDate(dateString: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val parsedDate = LocalDate.parse(dateString, inputFormatter)
        val currentYear = LocalDate.now().year
        // 입력 날짜에서 연도만 현재 연도로 바꾸기
        val updatedDate = parsedDate.withYear(currentYear)
        return updatedDate.format(outputFormatter)
    }


    fun formatNumber(number: String): String {
        return try {
            val num = number.toLong()
            NumberFormat.getNumberInstance(Locale.KOREA).format(num)
        } catch (e: Exception) {
            number
        }
    }

    fun showMultiLineCustomToast(context: Context, title: String, content: String) {
        val inflater = LayoutInflater.from(context)
        val binding = ToastMultiLineCustomBinding.inflate(inflater)

        binding.tvTitle.text = title
        binding.tvContent.text = content

//        if(title.contains("권한")) binding.layout.backgroundTintList = context.getColorStateList(R.color.white)
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = binding.root
        toast.setGravity(Gravity.BOTTOM, 0, 200)
        toast.show()
    }

    fun showSingleLineCustomToast(context: Context, type: ToastType, content: String) {
        val inflater = LayoutInflater.from(context)
        val binding = ToastSingleLineCustomBinding.inflate(inflater)

        binding.tvContent.text = content
        if(type == ToastType.ERROR) binding.icError.visibility = View.VISIBLE
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = binding.root
        toast.setGravity(Gravity.BOTTOM, 0, 228)
        toast.show()
    }

    fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    fun floorTenThousandDecimal(prev: BigDecimal, current: BigDecimal): BigDecimal {
        return prev.subtract(current)
            .divide(BigDecimal(10000), 0, RoundingMode.DOWN)
    }

    fun View.expandTouchArea(extraPadding: Int = 30) {
        val parentView = this.parent as? ViewGroup ?: return

        parentView.post {
            val rect = Rect()
            this.getHitRect(rect)
            rect.top -= extraPadding
            rect.bottom += extraPadding
            rect.left -= extraPadding
            rect.right += extraPadding
            parentView.touchDelegate = TouchDelegate(rect, this)
        }
    }
}

sealed class ToastType {
    object DEFAULT : ToastType()
    object ERROR : ToastType()
}