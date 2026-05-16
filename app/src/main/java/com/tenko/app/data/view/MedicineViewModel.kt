package com.tenko.app.data.view

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tenko.app.data.api.ApiClient
import com.tenko.app.data.model.MedicineStatus
import com.tenko.app.data.serializable.ReminderCreate
import com.tenko.app.data.serializable.ReminderResponse
import com.tenko.app.data.serializable.ReminderUpdate
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedicineViewModel : ViewModel() {
    // --- ESTADO DE DATOS (API) ---
    private val _medicines = MutableStateFlow<List<ReminderResponse>>(emptyList())
    val medicines: StateFlow<List<ReminderResponse>> = _medicines

    // --- FILTROS Y CONTEOS ---
    private val _filter = MutableStateFlow(MedicineStatus.PENDING)
    val filter: StateFlow<MedicineStatus> = _filter

    val filteredMedicines: StateFlow<List<ReminderResponse>> =
        combine(_medicines, _filter) { meds, filter ->
            when (filter) {
                MedicineStatus.ALL -> meds
                MedicineStatus.TAKEN -> meds.filter { it.status == 1 }
                MedicineStatus.SKIPPED -> meds.filter { it.status == 2 }
                MedicineStatus.PENDING -> meds.filter { it.status == 0 }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val counts: StateFlow<Map<MedicineStatus, Int>> =
        _medicines.map { meds ->
            mapOf(
                MedicineStatus.ALL to meds.size,
                MedicineStatus.TAKEN to meds.count { it.status == 1 },
                MedicineStatus.SKIPPED to meds.count { it.status == 2 },
                MedicineStatus.PENDING to meds.count { it.status == 0 }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // --- ESTADO DEL FORMULARIO ---
    var isLoading by mutableStateOf(false)
    var isSaved by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // --- OPERACIONES API ---

    fun fetchMedicationReminders() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response =
                    ApiClient.client.get("https://api-myst.onrender.com/reminders/medication")
                if (response.status == HttpStatusCode.OK) {
                    _medicines.value = response.body()
                    errorMessage = null
                }
            } catch (e: Exception) {
                errorMessage = "Error al conectar con el servidor"
            } finally {
                isLoading = false
            }
        }
    }

    fun saveMedication(medication: ReminderCreate, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response =
                    ApiClient.client.post("https://api-myst.onrender.com/reminders/medication") {
                        contentType(ContentType.Application.Json)
                        setBody(medication)
                    }

                if (response.status.isSuccess()) {
                    isSaved = true
                    Toast.makeText(
                        navController.context,
                        "Recordatorio guardado",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchMedicationReminders() // Refrescar lista
                    navController.popBackStack()
                    isSaved = false
                }
            } catch (e: Exception) {
                errorMessage = "Error al guardar: ${e.localizedMessage}"
                Toast.makeText(navController.context, errorMessage, Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }

    fun updateReminderStatus(id: Int, newStatus: Int) {
        viewModelScope.launch {
            try {
                val updateData = ReminderUpdate(status = newStatus)
                val response =
                    ApiClient.client.patch("https://api-myst.onrender.com/reminders/me/$id") {
                        contentType(ContentType.Application.Json)
                        setBody(updateData)
                    }
                if (response.status.isSuccess()) {
                    fetchMedicationReminders()
                }
            } catch (e: Exception) {
                errorMessage = "No se pudo actualizar el estado"
            }
        }
    }

    fun deleteReminder(id: Int) {
        viewModelScope.launch {
            try {
                val response =
                    ApiClient.client.delete("https://api-myst.onrender.com/reminders/me/$id")
                if (response.status.isSuccess()) {
                    fetchMedicationReminders()
                }
            } catch (e: Exception) {
                errorMessage = "Error al eliminar"
            }
        }
    }

    fun setFilter(filter: MedicineStatus) {
        _filter.value = filter
    }
}