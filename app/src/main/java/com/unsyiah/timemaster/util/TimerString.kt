package com.unsyiah.timemaster.util

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.unsyiah.timemaster.data.TimeMasterEvent
import java.util.*

fun decorateMillisLikeStopwatch(millis: Long) : String {
    val (hours, minutes, seconds) = convertMillisToHoursMinutesSeconds(millis)
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun decorateMillisWithDecimalHours(millis: Long) : String {
    val hms = convertMillisToHoursMinutesSeconds(millis)
    val hours = hms.first
    val minutes = hms.second
    val seconds = hms.third

    val decimalSeconds = seconds / 60f

    val decimalMinutes = (minutes + decimalSeconds ) / 60f

    val decimalHours = hours + decimalMinutes

    val precision = 2

    return "%.${precision}f".format(decimalHours)
}

fun decorateMillisWithWholeHoursAndMinutes(millis: Long) : String {
    val hms = convertMillisToHoursMinutesSeconds(millis)
    val hours = hms.first
    val minutes = hms.second
    val seconds = hms.third

    val roundedMinute = if (seconds >= 30) 1 else 0
    val addedMinutes = minutes + roundedMinute
    return "%d hours %d minutes".format(hours, addedMinutes)
}

fun getTimerString(currSeconds: Int) : String {
    val (hours, minutes, seconds) = convertSecondsToHoursMinutesSeconds(currSeconds)

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun calculateCurrSeconds(
    event: TimeMasterEvent?,
    currentTimeMillis: Long = System.currentTimeMillis()
): Int {
    if (event == null) return 0
    return ((currentTimeMillis - event.startTime) / 1000).toInt()
}

fun decorateMillisToDateString(millis: Long) : String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    val locale = Locale.getDefault()
    val monthString = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale)
    val dayString = cal.get(Calendar.DAY_OF_MONTH)
    val yearString = cal.get(Calendar.YEAR)
    return "%s %d, %d".format(monthString, dayString, yearString)
}

fun decorateMillisToTimeString(millis: Long, timeZone: TimeZone = TimeZone.getDefault()) : String {
    val cal = Calendar.getInstance(timeZone)
    cal.timeInMillis = millis
    val amPmString = if (cal.get(Calendar.AM_PM) == 0) "AM" else "PM"
    val calHour = cal.get(Calendar.HOUR)
    val hours = if (calHour == 0) 12 else calHour
    val minutes = cal.get(Calendar.MINUTE)
    return "%d:%02d %s".format(hours, minutes, amPmString)
}

fun selectAllValue(value: TextFieldValue): TextFieldValue {
    return TextFieldValue(
        text = value.text,
        selection = TextRange(0, value.text.length)
    )
}

fun cursorAtEnd(value: TextFieldValue): TextFieldValue {
    return TextFieldValue(
        text = value.text,
        selection = TextRange(value.text.length)
    )
}