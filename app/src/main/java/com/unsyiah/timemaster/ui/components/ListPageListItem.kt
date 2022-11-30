package com.unsyiah.timemaster.ui.components

import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.unsyiah.timemaster.R
import com.unsyiah.timemaster.util.convertHoursMinutesSecondsToMillis
import com.unsyiah.timemaster.util.decorateMillisLikeStopwatch
import com.unsyiah.timemaster.util.decorateMillisToTimeString
import com.unsyiah.timemaster.util.generateColorFromString

@Composable
fun ListPageListItemClosedContent(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
fun ListPageListItemOpenContent(
    eventName: String,
    startTime: Long,
    endTime: Long,
    onCancelButtonClick: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {}
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = eventName, style = MaterialTheme.typography.body1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ClockComponent(
                timeString = decorateMillisToTimeString(startTime),
                subtitle = stringResource(R.string.start_time)
            )
            ClockComponent(
                timeString = decorateMillisToTimeString(endTime),
                subtitle = stringResource(R.string.end_time)
            )
        }
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onCancelButtonClick
            ) {
                Text(
                    text = stringResource(R.string.button_cancel),
                    style = MaterialTheme.typography.body1
                )
            }
            OutlinedButton(
                onClick = onDeleteButtonClick
            ) {
                Text(
                    text = stringResource(R.string.button_delete),
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}


@Composable
fun ClockComponent(
    timeString: String,
    subtitle: String
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = timeString, style = MaterialTheme.typography.h4)
        Text(text = subtitle, style = MaterialTheme.typography.subtitle1)
    }
}



/*
* Tampilan
* Item List Saat Ter-Close atau dalam status tertutup
* */
@Preview(showBackground = true)
@Composable
fun ClosedListItem() {
    val titleName =
        "Ini adalah contoh judul, yang memastikan testing berhasil"
    val accentColor = generateColorFromString(titleName)
    val startTime = 0L
    val endTime = 10000L
    val elapsedTime = endTime - startTime
    val subtitleName = decorateMillisLikeStopwatch(elapsedTime)
    val closedContent = @Composable {
        ListPageListItemClosedContent(title = titleName, subtitle = subtitleName)
    }
    TimeClockListItem(
        accentColor = accentColor,
        onClick = {},
        closedContent = closedContent,
        openContent = {}
    )
}

/*
* Tampilan
* Item List Saat Dibuka dengan judul yang panjang
* */
@Preview(showBackground = true)
@Composable
fun OpenListItem() {
    val titleName =
        "Ini adalah contoh judul, yang memastikan testing berhasil"
    val accentColor = generateColorFromString(titleName)
    val startTime = 0L
    val endTime = convertHoursMinutesSecondsToMillis(1) + startTime
    val openContent = @Composable {
        ListPageListItemOpenContent(
            eventName = titleName,
            startTime = startTime,
            endTime = endTime
        )
    }
    TimeClockListItem(
        isClosed = false,
        accentColor = accentColor,
        onClick = {},
        closedContent = {},
        openContent = openContent
    )
}

/*
* Tampilan
* Item List Saat Dibuka dengan judul yang pendek
* */

@Preview(showBackground = true)
@Composable
fun OpenListItemShortTitle() {
    val titleName = "Belajar Kotlin"
    val accentColor = generateColorFromString(titleName)
    val startTime = 0L
    val endTime = convertHoursMinutesSecondsToMillis(1) + startTime
    val openContent = @Composable {
        ListPageListItemOpenContent(
            eventName = titleName,
            startTime = startTime,
            endTime = endTime
        )
    }
    TimeClockListItem(
        isClosed = false,
        accentColor = accentColor,
        onClick = {},
        closedContent = {},
        openContent = openContent
    )
}


/*
* Tampilan
* Item Clock Individu
* */

@Preview(showBackground = true)
@Composable
fun ClockItemPreview() {
    ClockComponent(
        timeString = "5:00 PM",
        subtitle = "Berakhir"
    )
}
