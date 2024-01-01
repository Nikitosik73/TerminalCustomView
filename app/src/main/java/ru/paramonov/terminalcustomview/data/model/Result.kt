package ru.paramonov.terminalcustomview.data.model

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("results") val resultListBars: List<Bar>
)
