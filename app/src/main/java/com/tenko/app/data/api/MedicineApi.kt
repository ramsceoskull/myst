package com.tenko.app.data.api

import com.tenko.app.data.serializable.DrugResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

class MedicineApi {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun searchMedicines(query: String): List<String> {
        if (query.isBlank()) return emptyList()

        val url =
            "https://api.fda.gov/drug/label.json?search=openfda.brand_name:$query&limit=10"

        return try {
            val response: DrugResponse = client.get(url).body()

            println("API Response: $response") // Debug log

            response.results
                .mapNotNull { it.openfda?.brand_name?.firstOrNull() }
                .distinct()

        } catch (e: Exception) {
            emptyList()
        }
    }
}
