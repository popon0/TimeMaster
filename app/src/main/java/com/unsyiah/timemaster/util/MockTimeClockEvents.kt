package com.unsyiah.timemaster.util

import com.unsyiah.timemaster.data.TimeMasterEvent
import com.unsyiah.timemaster.ui.viewmodel.ListRow

/**
 * Untuk melakukan testing dengan preview
 * data mock
 */
fun createMockTimeClockEventList(
    eventCount: Int = 5,
    eventNames: List<String> = listOf("Belajar Kotlin", "Membaca", "Project Akhir"),
    eventDurations: List<Long> = listOf(2 * MILLIS_PER_HOUR),
    durationsBetween: List<Long> = listOf(MILLIS_PER_HOUR),
): List<TimeMasterEvent> {
    val eventList = mutableListOf<TimeMasterEvent>()
    var startTime = 0L
    for(i in 0 until eventCount) {
        val endTime = startTime + eventDurations[i % eventDurations.size]
        eventList += TimeMasterEvent(
            eventNames[i % eventNames.size],
            startTime,
            endTime
        )
        startTime = endTime + durationsBetween[i % durationsBetween.size] // each event will be an hour apart from each other
    }
    return eventList.reversed()
}

val MockTimeClockEvents = createMockTimeClockEventList()
val MockTimeClockEventsGroupedByDate: Map<String, List<ListRow>> =
    groupEventsByDate(MockTimeClockEvents)