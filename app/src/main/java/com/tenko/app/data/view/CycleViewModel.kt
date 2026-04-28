package com.tenko.app.data.view

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenko.app.data.api.ApiClient
import com.tenko.app.data.serializable.* // Asegúrate de tener DailyLogCreate, CycleResponse, etc.
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CycleViewModel : ViewModel() {
    var cycles by mutableStateOf<List<CycleResponse>>(emptyList())
    var dailyLogs by mutableStateOf<List<DailyLogResponse>>(emptyList())
    var prediction by mutableStateOf<PredictionResponse?>(null)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchData() {
        viewModelScope.launch {
            isLoading = true
            fetchCycles()
            fetchDailyLogs()
            fetchPrediction()
            isLoading = false
        }
    }

    private suspend fun fetchCycles() {
        executeWithRetry {
            val response = ApiClient.client.get("https://api-myst.onrender.com/cycles/me")
            if (response.status == HttpStatusCode.OK) {
                cycles = response.body<List<CycleResponse>>().sortedByDescending { it.start_date }
                true
            } else false
        }
    }

    private suspend fun fetchDailyLogs() {
        executeWithRetry {
            val response = ApiClient.client.get("https://api-myst.onrender.com/daily-logs/me")
            if (response.status == HttpStatusCode.OK) {
                dailyLogs = response.body()
                true
            } else false
        }
    }

    private suspend fun fetchPrediction() {
        executeWithRetry {
            val response = ApiClient.client.get("https://api-myst.onrender.com/cycles/predict/me")
            if (response.status == HttpStatusCode.OK) {
                prediction = response.body()
                true
            } else false
        }
    }

    fun createDailyLog(logData: DailyLogCreate, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                val response = ApiClient.client.post("https://api-myst.onrender.com/daily-logs/") {
                    contentType(ContentType.Application.Json)
                    setBody(logData)
                }
                if (response.status.isSuccess()) {
                    fetchData()
                    onSuccess()
                    true
                } else false
            }
            isLoading = false
        }
    }

    private suspend fun executeWithRetry(action: suspend () -> Boolean) {
        var retryCount = 0
        while (retryCount < 3) {
            try {
                if (action()) {
                    errorMessage = null
                    return
                }
            } catch (e: Exception) {
                if (retryCount == 2) errorMessage = e.localizedMessage
            }
            retryCount++
            delay(1000)
        }
    }
}