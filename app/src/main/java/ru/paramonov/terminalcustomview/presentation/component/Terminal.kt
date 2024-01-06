package ru.paramonov.terminalcustomview.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
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
    modifier: Modifier = Modifier,
    bars: List<Bar>
) {
    var terminalState by rememberTerminalState(bars = bars)

    Chart(
        modifier = modifier,
        terminalState = terminalState,
        onChangeTerminalState = {
            terminalState = it
        }
    )

    bars.firstOrNull()?.let { bar ->
        Prices(
            modifier = modifier,
            maxPriceOnTime = terminalState.maxPriceOnTime,
            minPriceOnTime = terminalState.minPriceOnTime,
            lastPrice = bar.closePrice,
            pxPerCount = terminalState.pxPerCount
        )
    }
}

@Composable
private fun Chart(
    modifier: Modifier = Modifier,
    terminalState: TerminalState,
    onChangeTerminalState: (TerminalState) -> Unit
) {
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val visibleBarsCount = (terminalState.visibleBarsCount / zoomChange).roundToInt()
            .coerceIn(minimumValue = MIN_VISIBLE_BARS_COUNT, maximumValue = terminalState.bars.size)

        val scrolledBy = (terminalState.scrolledBy + panChange.x)
            .coerceIn(minimumValue = 0f, maximumValue = terminalState.bars.size * terminalState.barWidth - terminalState.terminalWidth)

        onChangeTerminalState(
            terminalState.copy(
                visibleBarsCount = visibleBarsCount,
                scrolledBy = scrolledBy
            )
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .clipToBounds()
            .padding(top = 32.dp, bottom = 32.dp, end = 32.dp)
            .transformable(state = transformableState)
            .onSizeChanged { size ->
                onChangeTerminalState(
                    terminalState.copy(
                        terminalWidth = size.width.toFloat(),
                        terminalHeight = size.height.toFloat()
                    )
                )
            }
    ) {
        val minPriceOnTime = terminalState.minPriceOnTime
        val pxPerCount = terminalState.pxPerCount

        translate(left = terminalState.scrolledBy) {
            terminalState.bars.forEachIndexed { index, bar ->
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

@Composable
private fun Prices(
    modifier: Modifier = Modifier,
    maxPriceOnTime: Float,
    minPriceOnTime: Float,
    lastPrice: Float,
    pxPerCount: Float
) {
    val textMeasurer = rememberTextMeasurer()
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .padding(vertical = 32.dp)
    ) {
        drawPriceLine(
            maxPriceOnTime = maxPriceOnTime,
            minPriceOnTime = minPriceOnTime,
            lastPrice = lastPrice,
            pxPerCount = pxPerCount,
            textMeasurer = textMeasurer
        )
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
        topLeft = Offset(x = size.width - textLayoutResult.size.width - 4.dp.toPx(), y = offsetY)
    )
}
