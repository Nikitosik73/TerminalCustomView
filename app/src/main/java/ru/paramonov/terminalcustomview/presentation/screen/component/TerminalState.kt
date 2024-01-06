package ru.paramonov.terminalcustomview.presentation.screen.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import ru.paramonov.terminalcustomview.data.model.Bar
import kotlin.math.roundToInt

@Suppress("UNCHECKED_CAST")
data class TerminalState(
    val bars: List<Bar>,
    val visibleBarsCount: Int = 100,
    val scrolledBy: Float = 0f,
    val terminalWidth: Float = 1f,
    val terminalHeight: Float = 1f
) {

    val barWidth: Float
        get() = terminalWidth / visibleBarsCount

    private val visibleBars: List<Bar>
        get() {
            val startIndex = (scrolledBy / barWidth).roundToInt().coerceAtLeast(minimumValue = 0)
            val endIndex = (startIndex + visibleBarsCount).coerceAtMost(maximumValue = bars.size)
            return bars.subList(fromIndex = startIndex, toIndex = endIndex)
        }

    val maxPriceOnTime: Float
        get() = visibleBars.maxOf { bar -> bar.highPrice }

    val minPriceOnTime: Float
        get() = visibleBars.minOf { bar -> bar.lowPrice }

    val pxPerCount: Float
        get() = terminalHeight / (maxPriceOnTime - minPriceOnTime)

    companion object {

        val TerminalSaver: Saver<MutableState<TerminalState>, Any> = listSaver(
            save = { state ->
                val terminalState = state.value
                listOf(
                    terminalState.bars,
                    terminalState.visibleBarsCount,
                    terminalState.scrolledBy,
                    terminalState.terminalWidth
                )
            },
            restore = {
                val terminalState = TerminalState(
                    bars = it[0] as List<Bar>,
                    visibleBarsCount = it[1] as Int,
                    scrolledBy = it[2] as Float,
                    terminalWidth = it[3] as Float
                )
                mutableStateOf(value = terminalState)
            }
        )
    }
}

@Composable
fun rememberTerminalState(bars: List<Bar>): MutableState<TerminalState> {
    return rememberSaveable(saver = TerminalState.TerminalSaver) {
        mutableStateOf(TerminalState(bars = bars))
    }
}
