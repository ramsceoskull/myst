package com.tenko.app.data.view

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenko.app.data.api.ApiClient
import com.tenko.app.data.serializable.* // Asegúrate de tener los nuevos serializables aquí
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LabViewModel : ViewModel() {
    // Estados de la UI
    var studies by mutableStateOf<List<LabStudyResponse>>(emptyList())
    var selectedStudy by mutableStateOf<LabStudyResponse?>(null)
    var evolutionData by mutableStateOf<ParameterEvolutionResponse?>(null)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val baseUrl = "https://api-myst.onrender.com/lab-studies"

    // --- OPERACIONES DE ESTUDIOS ---

    fun fetchMyStudies() {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                val response = ApiClient.client.get("$baseUrl/me")
                if (response.status == HttpStatusCode.OK) {
                    studies = response.body()
                    true
                } else false
            }
            isLoading = false
        }
    }

    fun createLabStudy(studyData: LabStudyCreate, context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                val response = ApiClient.client.post(baseUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(studyData)
                }
                if (response.status.isSuccess()) {
                    Toast.makeText(context, "Estudio guardado correctamente", Toast.LENGTH_SHORT)
                        .show()
                    fetchMyStudies()
                    onSuccess()
                    true
                } else false
            }
            isLoading = false
        }
    }

    fun deleteStudy(idStudy: Int, context: Context) {
        viewModelScope.launch {
            executeWithRetry {
                val response = ApiClient.client.delete("$baseUrl/me/$idStudy")
                if (response.status.isSuccess()) {
                    fetchMyStudies()
                    Toast.makeText(context, "Estudio eliminado", Toast.LENGTH_SHORT).show()
                    true
                } else false
            }
        }
    }

    // --- OPERACIONES DE EVOLUCIÓN (LONGITUDINAL) ---

    /**
     * Obtiene la evolución de un parámetro (ej: "Glucosa")
     * La API ya devuelve la tendencia (UP, DOWN, STABLE) calculada.
     */
    fun fetchParameterEvolution(parameter: String) {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                val response = ApiClient.client.get("$baseUrl/me/evolution/$parameter")
                if (response.status == HttpStatusCode.OK) {
                    evolutionData = response.body()
                    true
                } else {
                    evolutionData = null
                    false
                }
            }
            isLoading = false
        }
    }

    // --- OPERACIONES DE RESULTADOS INDIVIDUALES ---

    fun addResultToStudy(idStudy: Int, resultData: LabResultBase, context: Context) {
        viewModelScope.launch {
            executeWithRetry {
                val response = ApiClient.client.post("$baseUrl/me/$idStudy/results") {
                    contentType(ContentType.Application.Json)
                    setBody(resultData)
                }
                if (response.status.isSuccess()) {
                    // Si tenemos un estudio seleccionado, lo refrescamos
                    fetchMyStudies()
                    true
                } else false
            }
        }
    }

    // --- LÓGICA DE ROBUSTEZ (RETRY) ---

    private suspend fun executeWithRetry(action: suspend () -> Boolean) {
        var retryCount = 0
        val maxAttempts = 3
        var success = false

        while (retryCount < maxAttempts && !success) {
            try {
                val result = action()
                if (result) {
                    success = true
                    errorMessage = null
                } else {
                    retryCount++
                    delay(1000)
                }
            } catch (e: Exception) {
                retryCount++
                if (retryCount >= maxAttempts) {
                    errorMessage = "Error: ${e.localizedMessage}"
                }
                delay(1000)
            }
        }
    }
}