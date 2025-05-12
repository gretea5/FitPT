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
import java.time.format.DateTimeFormatter
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
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val birthDateTime = LocalDateTime.parse(dateString, formatter)
        val birthYear = birthDateTime.year
        val currentYear = LocalDate.now().year
        val koreanAge = currentYear - birthYear + 1
        return "${koreanAge}세"
    }


    fun dateformatYMDHMFromInt(year: Int, month: Int, day: Int):String{
//        val format = SimpleDateFormat("yyyy.MM.dd. HH:mm", Locale.KOREA)
//        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
//        return format.format(time)
        return "${year}.${month}.${day}."
    }

    //날짜 포맷 출력
    fun dateformatYMDHM(time:Date):String{
        val format = SimpleDateFormat("yyyy.MM.dd. HH:mm", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    fun dateformatYMD(time: Date):String{
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    fun formatLongToDate(longDate: Long): String {
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())  // 원하는 날짜 형식 지정
        return format.format(Date(longDate))  // Long 값을 Date 객체로 변환 후 포맷 적용
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