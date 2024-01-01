package ru.paramonov.terminalcustomview.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.paramonov.terminalcustomview.data.network.ApiFactory
import ru.paramonov.terminalcustomview.presentation.TerminalViewState

class TerminalViewModel : ViewModel() {

    private val apiService = ApiFactory.apiService

    private val _viewState = MutableStateFlow<TerminalViewState>(value = TerminalViewState.Initial)
    val viewState = _viewState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d("TerminalViewModel", "Exception caught: $throwable")
    }

    init {
        loadBars()
    }

    private fun loadBars() {
        viewModelScope.launch(exceptionHandler) {
            val results = apiService.loadBars().resultListBars
            _viewState.value = TerminalViewState.ContentTerminal(bars = results)
        }
    }
}