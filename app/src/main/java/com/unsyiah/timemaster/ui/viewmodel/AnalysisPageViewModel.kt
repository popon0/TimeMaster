package com.unsyiah.timemaster.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.unsyiah.timemaster.R
import com.unsyiah.timemaster.data.TimeMasterEvent
import com.unsyiah.timemaster.util.filterEventsByNumberOfDays
import com.unsyiah.timemaster.util.generateColorFromString
import com.unsyiah.timemaster.util.sortByNamesAndTotalMillis

class AnalysisPageViewModel (
    timeMasterEvents: LiveData<List<TimeMasterEvent>>,
    context: Context
): ViewModel() {

    val analysisPaneTransformation = Transformations.map(timeMasterEvents) { events ->
        state.analysisPanes.forEach { pane ->
            pane.onEventsUpdate(events)
        }
    }

    val events: List<TimeMasterEvent> = timeMasterEvents.value?: listOf()

    /**
     * Page overview keseluruhan waktu dari hari ini sampai bulanan
     */
    private val allTimePane = AnalysisPane(
        events = events,
        rangeName = context.getString(R.string.analysis_page_all_time)
    )
    private val todayPane = AnalysisPane(
        events = events,
        rangeName = context.getString(R.string.analysis_page_today),
        daysInRange = 1
    )
    private val lastWeekPane = AnalysisPane(
        events = events,
        rangeName = context.getString(R.string.analysis_page_last_week),
        daysInRange = 7
    )
    private val lastMonthPane = AnalysisPane(
        events = events,
        rangeName = context.getString(R.string.analysis_page_last_month),
        daysInRange = 30
    )

    val state = AnalysisPageViewModelState(
        analysisPanes = listOf(
            allTimePane,
            todayPane,
            lastWeekPane,
            lastMonthPane
        )
    )
}

class AnalysisPane(
    events: List<TimeMasterEvent>,
    val rangeName: String,
    private val daysInRange: Int = -1
) {
    var events by mutableStateOf(events)

    private lateinit var filteredEvents : List<TimeMasterEvent>
    private var totalMillis: Long = 0L
    lateinit var analysisRows: List<AnalysisRow>
    var selectedMillis by mutableStateOf(totalMillis)

    var selectedAnalysisRowId by mutableStateOf(-1L)

    init {
        onEventsUpdate(events)
    }

    fun onEventsUpdate(inputEvents: List<TimeMasterEvent>) {
        events = inputEvents
        filteredEvents = filterEventsByNumberOfDays(events, daysInRange)
        var millis = 0L
        filteredEvents.forEach {
            millis += it.endTime - it.startTime
        }
        totalMillis = millis
        selectedMillis = if (selectedAnalysisRowId == -1L) {
            totalMillis
        } else {
            // find the event that has been selected
            val matchingRow = analysisRows.find {
                selectedAnalysisRowId == it.id
            }
            matchingRow?.millis ?: totalMillis
        }
        analysisRows = (::sortByNamesAndTotalMillis)(filteredEvents)
    }

    fun changeSelectedAnalysisRowId(id: Long) {
        selectedAnalysisRowId =
            if (selectedAnalysisRowId == id) -1L else id
        selectedMillis = if (selectedAnalysisRowId == -1L) {
            totalMillis
        } else {
            // find the event that has been selected
            val matchingRow = analysisRows.find {
                id == it.id
            }
            matchingRow?.millis ?: totalMillis
        }
    }

    fun resetSelectedRowAndMillis() {
        selectedAnalysisRowId = -1
        selectedMillis = totalMillis
    }
}

class AnalysisPageViewModelState(
    val analysisPanes: List<AnalysisPane>,
    private var currAnalysisPaneIndex: Int = 0
) {
    var currAnalysisPane by mutableStateOf(analysisPanes[currAnalysisPaneIndex])
    val dateRangeStartButtonVisible : Boolean
        get() {
            return currAnalysisPaneIndex > 0
        }
    val dateRangeEndButtonVisible: Boolean
        get(){
            return currAnalysisPaneIndex < analysisPanes.size - 1
        }

    fun onDateRangeStartButtonClick() {
        if (currAnalysisPaneIndex > 0) {
            updateAnalysisPane(currAnalysisPaneIndex - 1)
        }
    }

    fun onDateRangeEndButtonClick() {
        if (currAnalysisPaneIndex < analysisPanes.size - 1) {
            updateAnalysisPane(currAnalysisPaneIndex + 1)
        }
    }

    private fun updateAnalysisPane(index: Int) {
        analysisPanes[currAnalysisPaneIndex].resetSelectedRowAndMillis()

        currAnalysisPaneIndex = index
        currAnalysisPane = analysisPanes[currAnalysisPaneIndex]
    }
}

class AnalysisRow(val name: String, val millis: Long, val id: Long) {
    val color = generateColorFromString(name)

    fun getPercentage(totalMillis: Long): Float {
        if(totalMillis <= 0L) return 0f
        return millis / totalMillis.toFloat() * 100f
    }
}
