package ru.paramonov.terminalcustomview.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.paramonov.terminalcustomview.presentation.screen.component.Terminal
import ru.paramonov.terminalcustomview.ui.theme.TerminalCustomViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TerminalCustomViewTheme {
                Terminal()
            }
        }
    }
}