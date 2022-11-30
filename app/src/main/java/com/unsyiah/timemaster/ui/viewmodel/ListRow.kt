package com.unsyiah.timemaster.ui.viewmodel

import com.unsyiah.timemaster.util.decorateMillisLikeStopwatch
import com.unsyiah.timemaster.util.generateColorFromString

class ListRow(
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val id: Long
) {
    val isRunning = startTime == endTime
    val color = generateColorFromString(title)
    val elapsedTime = endTime - startTime
    val elapsedTimeString = decorateMillisLikeStopwatch(elapsedTime)
}