package com.unsyiah.timemaster.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TimeClockListItem(
    modifier: Modifier = Modifier,
    isClosed: Boolean = true,
    accentColor: Color? = null,
    onClick: () -> Unit = {},
    closedContent: @Composable () -> Unit = {
        ListPageListItemClosedContent("Title", "Subtitle")
    },
    openContent: @Composable () -> Unit = {},
    openContentHeight: Dp = 180.dp
) {
    val itemHeight by animateDpAsState(
        targetValue = if (isClosed) TextFieldDefaults.MinHeight else openContentHeight
    )
    Box(
        modifier = modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .height(itemHeight)
    ) {
        Row {
            if (accentColor != null) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(5.dp)
                        .background(accentColor),
                )
            }
            Crossfade(targetState = isClosed) { closed ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(8.dp)
                ) {
                    if (closed) {
                        closedContent()
                    } else {
                        openContent()
                    }
                }
            }
        }
    }
}
