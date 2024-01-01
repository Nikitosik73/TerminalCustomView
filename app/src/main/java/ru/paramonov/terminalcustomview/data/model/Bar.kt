package ru.paramonov.terminalcustomview.data.model

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName
import java.util.Calendar
import java.util.Date

@Immutable
data class Bar(
    @SerializedName("o") val openPrice: Float,
    @SerializedName("c") val closePrice: Float,
    @SerializedName("h") val highPrice: Float,
    @SerializedName("l") val lowPrice: Float,
    @SerializedName("t") val timeOpen: Long
) {

    val calendar: Calendar
        get() {
            return Calendar.getInstance().apply {
                time = Date(timeOpen)
            }
        }
}
