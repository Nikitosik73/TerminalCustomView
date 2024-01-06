package ru.paramonov.terminalcustomview.presentation.screen.component

import ru.paramonov.terminalcustomview.data.model.Bar
import ru.paramonov.terminalcustomview.presentation.screen.TimeFrame

sealed class TerminalViewState {

    object Initial : TerminalViewState()

    object Loading : TerminalViewState()

    data class ContentTerminal(val bars: List<Bar>, val timeFrame: TimeFrame) : TerminalViewState()
}