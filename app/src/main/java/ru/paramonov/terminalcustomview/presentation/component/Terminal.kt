package ru.paramonov.terminalcustomview.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
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

    var scrolledBy by remember {
        mutableStateOf(0f)
    }

    var terminalIWidth by remember {
        mutableStateOf(0f)
    }

    val barWidth by remember {
        derivedStateOf {
            terminalIWidth / visibleBarsCount
        }
    }

    val visibleBars by remember {
        derivedStateOf {
            val startIndex = (scrolledBy / barWidth).roundToInt().coerceAtLeast(minimumValue = 0)
            val endIndex = (startIndex + visibleBarsCount).coerceAtMost(maximumValue = bars.size)
            bars.subList(fromIndex = startIndex, toIndex = endIndex)
        }
    }

    val transformableState = TransformableState { zoomChange, panChange, _ ->
        visibleBarsCount = (visibleBarsCount / zoomChange).roundToInt()
            .coerceIn(minimumValue = MIN_VISIBLE_BARS_COUNT, maximumValue = bars.size)

        scrolledBy = (scrolledBy + panChange.x)
            .coerceIn(minimumValue = 0f, maximumValue = bars.size * barWidth - terminalIWidth)
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .transformable(state = transformableState)
    ) {
        terminalIWidth = size.width
        val maxPriceOnTime = visibleBars.maxOf { bar -> bar.highPrice }
        val minPriceOnTime = visibleBars.minOf { bar -> bar.lowPrice }

        val pxPerCount = size.height / (maxPriceOnTime - minPriceOnTime)
        translate(left = scrolledBy) {
            bars.forEachIndexed { index, bar ->
                val offsetX = size.width - index * barWidth
                drawLine(
                    color = Color.White,
                    start = Offset(x = offsetX,y = size.height - ((bar.lowPrice - minPriceOnTime) * pxPerCount)),
                    end = Offset(x = offsetX, y = size.height - ((bar.highPrice - minPriceOnTime) * pxPerCount)),
                    strokeWidth = 1.dp.toPx()
                )
                val isIncreasePrice = bar.openPrice < bar.closePrice
                drawLine(
                    color = if (isIncreasePrice) Color.Green else Color.Red,
                    start = Offset(x = offsetX, y = size.height - ((bar.openPrice - minPriceOnTime) * pxPerCount)),
                    end = Offset(x = offsetX, y = size.height - ((bar.closePrice - minPriceOnTime) * pxPerCount)),
                    strokeWidth = barWidth / 2
                )
            }
        }
    }
}
