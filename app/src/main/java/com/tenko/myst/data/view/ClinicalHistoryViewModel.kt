package com.tenko.myst.data.view

import android.service.autofill.UserData
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tenko.myst.data.api.ApiClient
import com.tenko.myst.data.serializable.Token
import com.tenko.myst.data.serializable.ForgotPasswordRequest
import com.tenko.myst.data.serializable.ClinicalHistoryCreate
import com.tenko.myst.data.serializable.ClinicalHistoryUpdate
import com.tenko.myst.data.serializable.ClinicalHistoryResponse
import com.tenko.myst.navigation.AppScreens
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.launch

class ClinicalHistoryViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var tokenData by mutableStateOf<Token?>(null)

    fun createClinicalHistory(token: String, historyData: ClinicalHistoryCreate) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.post("https://api-myst.onrender.com/clinical-history/") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(historyData)
                }

                if (response.status == HttpStatusCode.OK) {
                    val createdHistory = response.body<ClinicalHistoryResponse>()
                    println("Historia clínica creada: ${createdHistory.id_history}")
                } else {
                    loginError = "Error: ${response.status.description}"
                }
            } catch (e: Exception) {
                loginError = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getMyClinicalHistory(token: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.get("https://api-myst.onrender.com/clinical-history/me") {
                    bearerAuth(token)
                }
                if (response.status == HttpStatusCode.OK) {
                    val history = response.body<ClinicalHistoryResponse>()
                    // Aquí guardarías 'history' en una variable de estado para mostrarla en la UI
                }
            } catch (e: Exception) {
                loginError = "No se pudo obtener la historia: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateClinicalHistory(token: String, updateData: ClinicalHistoryUpdate) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.patch("https://api-myst.onrender.com/clinical-history/me") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(updateData)
                }
                if (response.status == HttpStatusCode.OK) {
                    println("Historia clínica actualizada correctamente")
                }
            } catch (e: Exception) {
                loginError = "Error al actualizar: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateCycleStats(token: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.client.post("https://api-myst.onrender.com/clinical-history/me/backfill-stats") {
                    bearerAuth(token)
                }
                if (response.status == HttpStatusCode.OK) {
                    println("Estadísticas sincronizadas")
                }
            } catch (e: Exception) {
                println("Error en stats: ${e.localizedMessage}")
            }
        }
    }
}

