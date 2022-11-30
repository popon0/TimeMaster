package com.unsyiah.timemaster.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unsyiah.timemaster.data.TimeMasterEvent
import com.unsyiah.timemaster.ui.components.*
import com.unsyiah.timemaster.ui.viewmodel.AnalysisPageViewModelState
import com.unsyiah.timemaster.ui.viewmodel.AnalysisPane
import com.unsyiah.timemaster.util.MILLIS_PER_HOUR
import com.unsyiah.timemaster.util.decorateMillisWithDecimalHours
import com.unsyiah.timemaster.util.generateColorFromString
import com.unsyiah.timemaster.R

@Composable
fun AnalysisPage(
    viewModelState: AnalysisPageViewModelState
) {
    val analysisPageRows = viewModelState.currAnalysisPane.analysisRows
    val openId = viewModelState.currAnalysisPane.selectedAnalysisRowId
    val changeRowId = viewModelState.currAnalysisPane::changeSelectedAnalysisRowId
    val rangeName = viewModelState.currAnalysisPane.rangeName
    val selectedMillis = viewModelState.currAnalysisPane.selectedMillis

    Scaffold {
        Column(
            modifier = Modifier.padding(it)
        ) {
            TimeRangeSelector(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                centerText = rangeName,
                startButtonFunction = viewModelState::onDateRangeStartButtonClick,
                startButtonVisible = viewModelState.dateRangeStartButtonVisible,
                endButtonFunction = viewModelState::onDateRangeEndButtonClick,
                endButtonVisible = viewModelState.dateRangeEndButtonVisible
            )
            if (analysisPageRows.isNotEmpty()) {
                var totalMillis = 0L
                analysisPageRows.forEach {
                    totalMillis += it.millis
                }
                val segmentData = mutableListOf<Triple<Color, Float, Long>>()
                analysisPageRows.forEach {
                    val color = it.color
                    val percentage = it.getPercentage(totalMillis) / 100f
                    val id = it.id
                    segmentData.add(
                        Triple(
                            color, percentage, id
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 32.dp,
                            bottom = 32.dp
                        )
                ) {
                    PieChart(
                        segmentData = segmentData,
                        currId = openId
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // total hours recorded
                        Text(
                            modifier = Modifier.testTag("PieChart_CenterText_HoursValue"),
                            text = decorateMillisWithDecimalHours(selectedMillis),
                            style = MaterialTheme.typography.h3
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            style = MaterialTheme.typography.subtitle1,
                            text = stringResource(R.string.hours)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    analysisPageRows.forEach { row ->
                        val name = row.name
                        val id = row.id
                        val percentage = row.getPercentage(totalMillis)
                        item {
                            val percentageString = stringResource(R.string.percentage, percentage)
                            val isClosed = openId != id
                            TimeClockListItem(
                                modifier = Modifier.testTag("TimeClockListItem_${row.name}"),
                                isClosed = isClosed,
                                accentColor = generateColorFromString(name),
                                onClick = { changeRowId(id) },
                                closedContent = {
                                    AnalysisPageListItemContent(name, percentageString)
                                },
                                openContent = {
                                    AnalysisPageListItemContent(
                                        taskName = name,
                                        totalHours = percentageString,
                                        isClosed = false
                                    )
                                },
                                openContentHeight = 100.dp
                            )
                        }
                    }
                }
            } else {
                NothingHereText()
            }
        }
    }
}

@Preview
@Composable
fun AnalysisPageTest() {
    val testPane = AnalysisPane(
        events = listOf(
            TimeMasterEvent(
                startTime = 0L,
                endTime = MILLIS_PER_HOUR,
                name = "Belajar programming"
            )
        ),
        rangeName = "Mock Time"
    )
    AnalysisPage(
        viewModelState = AnalysisPageViewModelState(
            analysisPanes = listOf(testPane)
        )
    )
}
