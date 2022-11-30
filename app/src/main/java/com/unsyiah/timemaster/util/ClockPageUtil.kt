package com.unsyiah.timemaster.util

import kotlin.math.ceil

fun findEventStartTimeDelay(
    startTime: Long,
    tickFrequency: Long = 1000L,
    currentTime: Long = System.currentTimeMillis()
): Long {
    val totalEventTime = currentTime - startTime
    return tickFrequency - totalEventTime % tickFrequency
}

fun calculateCurrCountDownSeconds(
    countDownEndTime: Long,
    currentTime: Long = System.currentTimeMillis()
): Int {
    val remainingTime = countDownEndTime - currentTime
    return ceil(remainingTime.toFloat() / MILLIS_PER_SECOND.toFloat()).toInt()
}