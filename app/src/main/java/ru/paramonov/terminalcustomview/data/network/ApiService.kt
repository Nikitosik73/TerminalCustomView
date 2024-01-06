package ru.paramonov.terminalcustomview.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import ru.paramonov.terminalcustomview.data.model.Result

interface ApiService {

    @GET("aggs/ticker/AAPL/range/{$TIME_FRAME}/2023-01-01/2024-01-01?adjusted=true&sort=desc&limit=50000&apiKey=fRrpu3FjJixOINdzFcgmIKJCNZWfFaPr")
    suspend fun loadBars(
        @Path(TIME_FRAME) timeFrame: String
    ): Result

    companion object {
        private const val TIME_FRAME = "timeFrame"
    }
}