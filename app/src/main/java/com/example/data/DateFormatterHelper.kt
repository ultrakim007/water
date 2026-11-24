package com.example.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class KoreanDateHeader(
    val dateIso: String,
    val yearText: String,
    val monthDayAndWeekday: String
)

object DateFormatterHelper {

    fun currentDateHeader(nowMillis: Long = System.currentTimeMillis()): KoreanDateHeader {
        val date = Date(nowMillis)
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val yearFormatter = SimpleDateFormat("yyyy년", Locale.KOREA)
        val mainFormatter = SimpleDateFormat("M월 d일 EEEE", Locale.KOREA)

        return KoreanDateHeader(
            dateIso = isoFormatter.format(date),
            yearText = yearFormatter.format(date),
            monthDayAndWeekday = mainFormatter.format(date)
        )
    }

    fun formatKoreanTime(timestampMillis: Long?): String? {
        if (timestampMillis == null) return null
        val formatter = SimpleDateFormat("a h:mm", Locale.KOREA)
        return formatter.format(Date(timestampMillis))
    }
}
