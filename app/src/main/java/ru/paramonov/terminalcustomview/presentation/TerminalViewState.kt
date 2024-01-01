package ru.paramonov.terminalcustomview.presentation

import ru.paramonov.terminalcustomview.data.model.Bar

sealed class TerminalViewState {

    object Initial : TerminalViewState()

    data class ContentTerminal(val bars: List<Bar>) : TerminalViewState()
}