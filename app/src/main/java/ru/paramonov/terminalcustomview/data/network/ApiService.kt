package ru.paramonov.terminalcustomview.data.network

import retrofit2.http.GET
import ru.paramonov.terminalcustomview.data.model.Result

interface ApiService {

    @GET("aggs/ticker/AAPL/range/30/minute/2023-01-01/2024-01-01?adjusted=true&sort=asc&limit=50000&apiKey=fRrpu3FjJixOINdzFcgmIKJCNZWfFaPr")
    suspend fun loadBars(): Result
}