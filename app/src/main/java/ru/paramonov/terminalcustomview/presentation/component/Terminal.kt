package ru.paramonov.terminalcustomview.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.paramonov.terminalcustomview.data.model.Bar

@Composable
fun Terminal(
    bars: List<Bar>
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        // ширина свечи
        val barWidth = size.width / bars.size
        // максимальная стоимость за всё время
        val maxPriceOnTime = bars.maxOf { bar -> bar.highPrice }
        // минимальная стоимость за всё время
        val minPriceOnTime = bars.minOf { bar -> bar.lowPrice }
        // кол-во пискселей на один доллар
        val pxPerPoint = size.height / (maxPriceOnTime - minPriceOnTime)

        bars.forEachIndexed { index, bar ->
            val offsetX = index * barWidth
            drawLine(
                color = Color.White,
                start = Offset(x = offsetX, y = size.height - (bar.lowPrice - minPriceOnTime) * pxPerPoint),
                end = Offset(x = offsetX, y = size.height - (bar.highPrice - minPriceOnTime) * pxPerPoint),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}