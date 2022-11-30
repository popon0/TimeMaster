package com.unsyiah.timemaster.util

import com.unsyiah.timemaster.data.TimeMasterEvent
import com.unsyiah.timemaster.ui.viewmodel.AnalysisRow
import com.unsyiah.timemaster.ui.viewmodel.ListRow
import java.util.*

fun groupEventsByDate(events: List<TimeMasterEvent>): Map<String, List<ListRow>> {
    val rows = events.map {
        ListRow(it.name, it.startTime, it.endTime, it.id)
    }
    return rows.groupBy {
        decorateMillisToDateString(it.startTime)
    }
}

fun getAutofillValues(events: List<TimeMasterEvent>): Set<String> {
    return events.map {
        it.name
    }.toSet()
}

fun sortByNamesAndTotalMillis(events: List<TimeMasterEvent>): List<AnalysisRow> {
    val eventNameAndDurationMap : MutableMap<String, Long> = mutableMapOf()
    for (event in events) {
        if (event.isRunning) continue
        val name = event.name
        val duration = event.endTime - event.startTime
        if (eventNameAndDurationMap[name] == null) {
            eventNameAndDurationMap[name] = duration
        } else {
            val currentDuration = eventNameAndDurationMap[name]!!
            eventNameAndDurationMap[name] = currentDuration + duration
        }
    }
    val eventDurationList = eventNameAndDurationMap.toList().sortedByDescending { it.second }
    var i = -1L
    return eventDurationList.map { pair ->
        i++
        AnalysisRow(pair.first, pair.second, i)
    }
}

fun filterEventsByNumberOfDays(
    events: List<TimeMasterEvent>,
    numberOfDays: Int = -1
): List<TimeMasterEvent> {
    if (numberOfDays < 0) return events
    val now = System.currentTimeMillis()
    val lastMidnight = findPreviousMidnight()
    val firstDayMillis = now - lastMidnight
    val cutoffMillis = convertHoursMinutesSecondsToMillis(
        (numberOfDays - 1) * 24
    ) + firstDayMillis
    return events.filter { it.startTime > now - cutoffMillis }
}

fun findPreviousMidnight(): Long {
    val date = Calendar.getInstance()
    date.set(Calendar.HOUR_OF_DAY, 0)
    date.set(Calendar.MINUTE, 0)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
    return date.timeInMillis
}
