package com.tenko.app.data.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenko.app.data.api.ApiClient
import com.tenko.app.data.serializable.AddressCreate
import com.tenko.app.data.serializable.AddressResponse
import com.tenko.app.data.serializable.AddressUpdate
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

class AddressViewModel : ViewModel() {
    var addresses by mutableStateOf<List<AddressResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Obtener todas mis direcciones (@router.get("/me"))
    fun fetchMyAddresses() {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                val response = ApiClient.client.get("https://api-myst.onrender.com/addresses/me")
                if (response.status == HttpStatusCode.OK) {
                    addresses = response.body()
                    true
                } else false
            }
            isLoading = false
        }
    }

    // Crear nueva dirección (@router.post("/"))
    fun createAddress(addressData: AddressCreate, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                val response = ApiClient.client.post("https://api-myst.onrender.com/addresses/") {
                    contentType(ContentType.Application.Json)
                    setBody(addressData)
                }
                if (response.status.isSuccess()) {
                    fetchMyAddresses()
                    onSuccess()
                    true
                } else false
            }
            isLoading = false
        }
    }

    // Actualizar dirección (@router.patch("/me/{id_address}"))
    fun updateAddress(idAddress: Int, updateData: AddressUpdate, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                val response =
                    ApiClient.client.patch("https://api-myst.onrender.com/addresses/me/$idAddress") {
                        contentType(ContentType.Application.Json)
                        setBody(updateData)
                    }

                if (response.status.isSuccess()) {
                    // Refrescamos la lista para ver los cambios reflejados (especialmente si cambió is_selected)
                    fetchMyAddresses()
                    onSuccess()
                    true
                } else {
                    false
                }
            }
            isLoading = false
        }
    }

    fun deleteAddress(idAddress: Int) {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                val response =
                    ApiClient.client.delete("https://api-myst.onrender.com/addresses/me/$idAddress")

                if (response.status == HttpStatusCode.OK) {
                    // Actualizamos la lista local eliminando la dirección sin necesidad de re-consultar al server
                    addresses = addresses.filter { it.id_address != idAddress }
                    true
                } else {
                    false
                }
            }
            isLoading = false
        }
    }

    // Seleccionar dirección activa (@router.post("/me/{id}/select"))
    fun selectAddress(idAddress: Int) {
        viewModelScope.launch {
            isLoading = true
            executeWithRetry {
                val response =
                    ApiClient.client.post("https://api-myst.onrender.com/addresses/me/$idAddress/select")
                if (response.status.isSuccess()) {
                    fetchMyAddresses()
                    true
                } else false
            }
            isLoading = false
        }
    }

    fun unselectAllAddresses() {
        viewModelScope.launch {
            isLoading = true
            // Filtramos las que están seleccionadas para mandar el parche al server
            val selectedAddresses = addresses.filter { it.is_selected }

            selectedAddresses.forEach { address ->
                executeWithRetry {
                    val response =
                        ApiClient.client.patch("https://api-myst.onrender.com/addresses/me/${address.id_address}") {
                            contentType(ContentType.Application.Json)
                            // Enviamos solo el cambio del booleano
                            setBody(mapOf("is_selected" to false))
                        }
                    response.status.isSuccess()
                }
            }
            // Refrescamos la lista final desde el servidor
            fetchMyAddresses()
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