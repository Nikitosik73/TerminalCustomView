package ru.paramonov.terminalcustomview.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.paramonov.terminalcustomview.presentation.component.Terminal
import ru.paramonov.terminalcustomview.presentation.viewmodel.TerminalViewModel
import ru.paramonov.terminalcustomview.ui.theme.TerminalCustomViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TerminalCustomViewTheme {
                val viewModel: TerminalViewModel = viewModel()
                val viewState: State<TerminalViewState> = viewModel.viewState.collectAsState()
                when (val currentState = viewState.value) {
                    is TerminalViewState.ContentTerminal -> {
                        Terminal(bars = currentState.bars)
                    }
                    is TerminalViewState.Initial -> {}
                }
            }
        }
    }
}