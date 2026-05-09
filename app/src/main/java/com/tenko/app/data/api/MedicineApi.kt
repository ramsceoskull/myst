package com.tenko.app.data.api

/*
class MedicineApi {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun searchMedicines(query: String): List<String> {
        if (query.isBlank()) return emptyList()

        val url = "https://api.fda.gov/drug/label.json?search=openfda.brand_name:\"$query\"&limit=10"

        return try {
            val response: DrugResponse = client.get(url).body()

            response.results
                .mapNotNull { it.openfda?.brand_name?.firstOrNull() }
                .distinct()

        } catch (e: Exception) {
            emptyList()
        }
    }
}*/
