package ru.paramonov.terminalcustomview.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import ru.paramonov.terminalcustomview.data.model.Bar
import kotlin.math.roundToInt

private const val MIN_VISIBLE_BARS_COUNT = 20

@Composable
fun Terminal(
    bars: List<Bar>
) {
    var terminalState by rememberTerminalState(bars = bars)

    val transformableState = TransformableState { zoomChange, panChange, _ ->
        val visibleBarsCount = (terminalState.visibleBarsCount / zoomChange).roundToInt()
            .coerceIn(minimumValue = MIN_VISIBLE_BARS_COUNT, maximumValue = bars.size)

        val scrolledBy = (terminalState.scrolledBy + panChange.x)
            .coerceIn(minimumValue = 0f, maximumValue = bars.size * terminalState.barWidth - terminalState.terminalWidth)

        terminalState = terminalState.copy(
            visibleBarsCount = visibleBarsCount,
            scrolledBy = scrolledBy
        )
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .transformable(state = transformableState)
            .onSizeChanged { size ->
                terminalState = terminalState.copy(terminalWidth = size.width.toFloat())
            }
    ) {
        val maxPriceOnTime = terminalState.visibleBars.maxOf { bar -> bar.highPrice }
        val minPriceOnTime = terminalState.visibleBars.minOf { bar -> bar.lowPrice }

        val pxPerCount = size.height / (maxPriceOnTime - minPriceOnTime)
        translate(left = terminalState.scrolledBy) {
            bars.forEachIndexed { index, bar ->
                val offsetX = size.width - index * terminalState.barWidth
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
                    strokeWidth = terminalState.barWidth / 2
                )
            }
        }
    }
}
