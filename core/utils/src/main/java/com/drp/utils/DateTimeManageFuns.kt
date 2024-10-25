package com.drp.utils

import android.annotation.SuppressLint
import android.os.Build
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.GregorianCalendar

fun millisToDateConvert(time: Long): String {
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val jalaliCalendar = JalaliCalendar(
        GregorianCalendar(
            calendar.get(Calendar.YEAR), calendar.get(
                Calendar.MONTH
            ), calendar.get(Calendar.DAY_OF_MONTH)
        )
    )
    return ("$jalaliCalendar")
}

@SuppressLint("SimpleDateFormat")
fun millisToTimeConvert(time: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss")
    return (sdf.format(time))
}


fun validateDateInTwoMonthRange(millis: Long): Boolean {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val selectedDateInstant = Instant.ofEpochMilli(millis)
        val selectedDateTime =
            LocalDateTime.ofInstant(selectedDateInstant, ZoneId.systemDefault())

        val currentDate = LocalDateTime.now()

        val twoMonthsAgo = currentDate.minusMonths(2)

        return !selectedDateTime.isBefore(twoMonthsAgo)
    } else {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -2)
        return (cal.timeInMillis < millis)
    }


}


fun isTwoMillisInSpecificRange(from: Long, to: Long, dayRange: Int): Boolean {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val fromLocalDateTime =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(from), ZoneId.systemDefault())

        val toLocalDateTime =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(to), ZoneId.systemDefault())


        val daysBetween = ChronoUnit.DAYS.between(fromLocalDateTime, toLocalDateTime)
        daysBetween <= dayRange
    } else {

        val differenceInMillis = to - from

        val differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24)

        differenceInDays <= dayRange
    }
}
fun timeStampDateConvert(time: Long): String {
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val jalaliCalendar = JalaliCalendar(
        GregorianCalendar(
            calendar.get(Calendar.YEAR), calendar.get(
                Calendar.MONTH
            ), calendar.get(Calendar.DAY_OF_MONTH)
        )
    )
    return ("$jalaliCalendar")
}
@SuppressLint("SimpleDateFormat")
fun timeStampToTimeConvert(time: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss")
    return (sdf.format(time))
}
