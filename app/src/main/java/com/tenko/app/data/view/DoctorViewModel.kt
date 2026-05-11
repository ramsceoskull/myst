package com.tenko.app.data.view

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tenko.app.data.api.ApiClient
import com.tenko.app.data.serializable.ContactCreate
import com.tenko.app.data.serializable.ContactResponse
import com.tenko.app.data.serializable.ContactUpdate
import com.tenko.app.data.serializable.ReminderResponse
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {
    var contacts by mutableStateOf<List<ContactResponse>>(emptyList())
    var allReminders by mutableStateOf<List<ReminderResponse>>(emptyList())
    var filteredReminders by mutableStateOf<List<ReminderResponse>>(emptyList())

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var currentStep by mutableIntStateOf(0) // Para controlar el paso actual en la UI (contactos, recordatorios, etc.)
        private set

    fun nextStep() {
        currentStep++
    }

    fun previousStep() {
        if (currentStep > 0) currentStep--
    }

    fun fetchContacts() {
        viewModelScope.launch {
            executeWithRetry {
                val response = ApiClient.client.get("https://api-myst.onrender.com/contacts/me")
                if (response.status == HttpStatusCode.OK) {
                    println("Fetch contacts successful: ${response.status}")
                    contacts = response.body()
                    true
                } else false
            }
        }
    }

    fun createContact(contactData: ContactCreate, context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                val response = ApiClient.client.post("https://api-myst.onrender.com/contacts/") {
                    contentType(ContentType.Application.Json)
                    setBody(contactData)
                }
                if (response.status.isSuccess()) {
                    Toast.makeText(context, "Doctor agregado exitosamente", Toast.LENGTH_SHORT)
                        .show()
                    fetchContacts()
                    onSuccess()
                    true
                } else {
                    Toast.makeText(
                        context,
                        "Error al agregar doctor: ${response.status}",
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }
            }
            isLoading = false
        }
    }

    fun updateContact(idContact: Int, updateData: ContactUpdate, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                // PATCH a /contacts/me/{id}
                val response =
                    ApiClient.client.patch("https://api-myst.onrender.com/contacts/me/$idContact") {
                        contentType(ContentType.Application.Json)
                        setBody(updateData)
                    }

                if (response.status.isSuccess()) {
                    fetchContacts() // Refrescamos la lista para ver los cambios
                    onSuccess()
                    true
                } else false
            }
            isLoading = false
        }
    }

    fun deleteContact(idContact: Int, navController: NavController) {
        viewModelScope.launch {
            executeWithRetry {
                val response =
                    ApiClient.client.delete("https://api-myst.onrender.com/contacts/me/$idContact")
                if (response.status.isSuccess()) {
                    fetchContacts()
                    Toast.makeText(
                        navController.context,
                        "Doctor eliminado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                } else {
                    Toast.makeText(
                        navController.context,
                        "Error al eliminar doctor: ${response.status}",
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }
            }
        }
    }

    fun fetchReminders() {
        viewModelScope.launch {
            executeWithRetry {
                val response = ApiClient.client.get("https://api-myst.onrender.com/reminders/me")
                if (response.status == HttpStatusCode.OK) {
                    allReminders = response.body()
                    true
                } else false
            }
        }
    }

    fun fetchContactReminders(id_contact: Int) {
        viewModelScope.launch {
            executeWithRetry {
                val response =
                    ApiClient.client.get("https://api-myst.onrender.com/reminders/contact/${id_contact}")
                if (response.status == HttpStatusCode.OK) {
                    allReminders = response.body()
                    true
                } else false
            }
        }
    }

    /**
     * Filtra los recordatorios que pertenecen a un médico específico
     */
    fun filterRemindersByContact(idContact: Int) {
        filteredReminders = allReminders.filter { it.id_contact == idContact }
    }

    // --- LÓGICA DE ROBUSTEZ (RETRY PARA DATASTORE) ---

    /**
     * Esta función encapsula el reintento. Si el token no está listo (401),
     * espera un segundo y vuelve a intentar hasta 3 veces.
     */
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
                    delay(1000) // Espera para que el DataStore suelte el token
                }
            } catch (e: Exception) {
                retryCount++
                if (retryCount >= maxAttempts) {
                    errorMessage = "Error de conexión: ${e.localizedMessage}"
                }
                delay(1000)
            }
        }
    }
}