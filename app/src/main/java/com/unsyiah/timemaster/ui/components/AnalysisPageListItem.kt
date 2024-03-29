package com.unsyiah.timemaster.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.unsyiah.timemaster.util.convertHoursMinutesSecondsToMillis
import com.unsyiah.timemaster.util.decorateMillisWithDecimalHours
import com.unsyiah.timemaster.util.generateColorFromString

@Composable
fun AnalysisPageListItemContent(
    taskName: String,
    totalHours: String,
    isClosed: Boolean = true
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),

    ) {
        val (name, time) = createRefs()
        val maxLines = if(isClosed) 1 else Int.MAX_VALUE
        Text(
            modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start)
                end.linkTo(time.start)
                centerVerticallyTo(parent)
                width = Dimension.fillToConstraints
            },
            text = taskName,
            style = MaterialTheme.typography.body1,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.constrainAs(time) {
                start.linkTo(name.end)
                end.linkTo(parent.end)
                centerVerticallyTo(parent)
            },
            text = totalHours,
            maxLines = 1,
            style = MaterialTheme.typography.h6,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ClosedAnalysisPageListItem() {
    val titleName = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis porttitor ligula efficitur purus varius, quis bibendum orci aliquam. Cras sem mi, vestibulum sed aliquam eu, lobortis in orci"
    val accentColor = generateColorFromString(titleName)
    val totalTime = decorateMillisWithDecimalHours(
        convertHoursMinutesSecondsToMillis(10)
    )
    val totalTimeString =
        if (totalTime.toFloat() == 1f) "$totalTime hr" else "$totalTime hrs"
    val closedContent = @Composable {
        AnalysisPageListItemContent(titleName, totalTimeString)
    }
    TimeClockListItem(
        accentColor = accentColor,
        onClick = {},
        closedContent = closedContent,
        openContent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun OpenAnalysisPageListItem() {
    val titleName = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis porttitor ligula efficitur purus varius, quis bibendum orci aliquam."
    val accentColor = generateColorFromString(titleName)
    val totalTime = decorateMillisWithDecimalHours(
        convertHoursMinutesSecondsToMillis(10)
    )
    val totalTimeString =
        if (totalTime.toFloat() == 1f) "$totalTime hr" else "$totalTime hrs"
    val openContent = @Composable {
        AnalysisPageListItemContent(titleName, totalTimeString, false)
    }
    TimeClockListItem(
        accentColor = accentColor,
        onClick = {},
        closedContent = {},
        openContent = openContent,
        isClosed = false,
        openContentHeight = 100.dp
    )
}