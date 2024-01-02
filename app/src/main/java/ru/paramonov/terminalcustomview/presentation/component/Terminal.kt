package ru.paramonov.terminalcustomview.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.paramonov.terminalcustomview.data.model.Bar
import kotlin.math.roundToInt

private const val MIN_VISIBLE_BARS_COUNT = 20

@Composable
fun Terminal(
    bars: List<Bar>
) {
    var visibleBarsCount by remember {
        mutableStateOf(value = 100)
    }

    val transformableState = TransformableState { zoomChange, _, _ ->
        visibleBarsCount = (visibleBarsCount / zoomChange).roundToInt()
            .coerceIn(minimumValue = MIN_VISIBLE_BARS_COUNT, maximumValue = bars.size)
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .transformable(state = transformableState)
    ) {
        val maxPriceOnTime = bars.maxOf { bar -> bar.highPrice }
        val minPriceOnTime = bars.minOf { bar -> bar.lowPrice }

        val barWidth = size.width / visibleBarsCount
        val pxPerCount = size.height / (maxPriceOnTime - minPriceOnTime)

        bars.take(n = visibleBarsCount).forEachIndexed { index, bar ->
            val offsetX = size.width - index * barWidth
            drawLine(
                color = Color.White,
                start = Offset(x = offsetX, y = size.height - ((bar.lowPrice - minPriceOnTime) * pxPerCount)),
                end = Offset(x = offsetX, y = size.height - ((bar.highPrice - minPriceOnTime) * pxPerCount)),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}