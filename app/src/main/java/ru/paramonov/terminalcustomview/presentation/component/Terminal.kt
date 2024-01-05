package ru.paramonov.terminalcustomview.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val textMeasured = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(top = 32.dp, bottom = 32.dp)
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
        bars.firstOrNull()?.let { bar ->
            drawPriceLine(
                maxPriceOnTime = maxPriceOnTime,
                minPriceOnTime = minPriceOnTime,
                lastPrice = bar.closePrice,
                pxPerCount = pxPerCount,
                textMeasurer = textMeasured
            )
        }
    }
}

private fun DrawScope.drawPriceLine(
    maxPriceOnTime: Float,
    minPriceOnTime: Float,
    lastPrice: Float,
    pxPerCount: Float,
    textMeasurer: TextMeasurer
) {
    // max price
    val maxPriceOffsetY = 0.dp.toPx()
    drawDashedLine(
        start = Offset(x = 0.dp.toPx(), y = maxPriceOffsetY),
        end = Offset(x = size.width, maxPriceOffsetY)
    )
    drawTextPrice(
        price = maxPriceOnTime,
        textMeasurer = textMeasurer,
        offsetY = maxPriceOffsetY
    )
    // last price
    val lastPriceOffsetY = size.height - ((lastPrice - minPriceOnTime) * pxPerCount)
    drawDashedLine(
        start = Offset(x = 0.dp.toPx(), y = lastPriceOffsetY),
        end = Offset(x = size.width, y = lastPriceOffsetY)
    )
    drawTextPrice(
        price = lastPrice,
        textMeasurer = textMeasurer,
        offsetY = lastPriceOffsetY
    )
    // min price
    val minPriceOffsetY = size.height
    drawDashedLine(
        start = Offset(x = 0.dp.toPx(), y = minPriceOffsetY),
        end = Offset(x = size.width, y = minPriceOffsetY)
    )
    drawTextPrice(
        price = minPriceOnTime,
        textMeasurer = textMeasurer,
        offsetY = minPriceOffsetY
    )
}

private fun DrawScope.drawDashedLine(
    color: Color = Color.White,
    start: Offset,
    end: Offset,
    strokeWidth: Float = 1f
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                4.dp.toPx(), 4.dp.toPx()
            )
        )
    )
}

private fun DrawScope.drawTextPrice(
    price: Float,
    textMeasurer: TextMeasurer,
    offsetY: Float,
    fontSize: TextUnit = 12.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = Color.White
) {
    val textLayoutResult = textMeasurer.measure(
        text = price.toString(),
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = color
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(x = size.width - textLayoutResult.size.width, y = offsetY)
    )
}
