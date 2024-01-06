package ru.paramonov.terminalcustomview.presentation.screen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.paramonov.terminalcustomview.data.network.ApiFactory
import ru.paramonov.terminalcustomview.presentation.screen.TimeFrame
import ru.paramonov.terminalcustomview.presentation.screen.component.TerminalViewState

class TerminalViewModel : ViewModel() {

    private val apiService = ApiFactory.apiService

    private val _viewState = MutableStateFlow<TerminalViewState>(value = TerminalViewState.Initial)
    val viewState = _viewState.asStateFlow()

    private var lastState: TerminalViewState = TerminalViewState.Initial

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d("TerminalViewModel", "Exception caught: $throwable")
        _viewState.value = lastState
    }

    init {
        loadBars()
    }

    fun loadBars(timeFrame: TimeFrame = TimeFrame.MIN_30) {
        lastState = _viewState.value
        _viewState.value = TerminalViewState.Loading
        viewModelScope.launch(exceptionHandler) {
            val results = apiService.loadBars(timeFrame = timeFrame.value).resultListBars
            _viewState.value = TerminalViewState.ContentTerminal(
                bars = results,
                timeFrame = timeFrame
            )
        }
    }
}