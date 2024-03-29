package com.unsyiah.timemaster.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp


@Composable
fun PieChart(
    segmentData: List<Triple<Color, Float, Long>> = listOf(),
    currId: Long = -1L
) {
    val stroke = with(LocalDensity.current) { Stroke(15.dp.toPx()) }

    val currentState = remember {
        MutableTransitionState(AnimatedCircleProgress.START)
            .apply { targetState = AnimatedCircleProgress.END }
    }
    val transition = updateTransition(currentState, label = "")
    val animationDelay = 400
    val animationDuration = 500
    val customTween : TweenSpec<Float> = tween(
        delayMillis = animationDelay,
        durationMillis = animationDuration,
        easing = FastOutSlowInEasing
    )

    val pieSegment by transition.animateFloat(
        transitionSpec = { customTween },
        label = ""
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            360f
        }
    }
    val shift by transition.animateFloat(
        transitionSpec = { customTween },
        label = ""
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            -45f
        } else {
            0f
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        val innerRadius = (size.minDimension) / 2
        val halfSize = size / 2.0f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val size = Size(innerRadius * 2, innerRadius * 2)
        var startAngle = shift - 90f
        segmentData.forEach { row ->
            val color = row.first
            val percentage = row.second * pieSegment
            val alpha = if (currId == row.third || currId == -1L) 1.0f else 0.5f
            drawArc(
                style = stroke,
                color = color,
                startAngle = startAngle,
                sweepAngle = percentage,
                size = size,
                topLeft = topLeft,
                useCenter = false,
                alpha = alpha
            )
            startAngle += percentage
        }
    }
}

private enum class AnimatedCircleProgress { START, END }
