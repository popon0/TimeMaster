package com.unsyiah.timemaster.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unsyiah.timemaster.R

@Composable
fun StartTimerButton(
    modifier: Modifier = Modifier,
    clockEnabled: Boolean,
    isRunning: Boolean,
    startClock: () -> Unit,
    stopClock: () -> Unit
) {
    Button(
        modifier = modifier
            .width(150.dp)
            .height(70.dp),
        shape = RoundedCornerShape(30.dp),
        enabled = clockEnabled,
        onClick = { if (isRunning) stopClock() else startClock() }
    ) {
        Text(
            text = if (isRunning) stringResource(id = R.string.stop) else stringResource(id = R.string.start),
            fontSize = 24.sp
        )
    }
}

@Preview
@Composable
fun NotEnabledNotRunning() {
    StartTimerButton(
        clockEnabled = false,
        isRunning = false,
        startClock = {},
        stopClock = {}
    )
}

@Preview
@Composable
fun EnabledNotRunning() {
    StartTimerButton(
        clockEnabled = true,
        isRunning = false,
        startClock = {},
        stopClock = {}
    )
}

@Preview
@Composable
fun EnabledAndRunning() {
    StartTimerButton(
        clockEnabled = true,
        isRunning = true,
        startClock = {},
        stopClock = {}
    )
}
