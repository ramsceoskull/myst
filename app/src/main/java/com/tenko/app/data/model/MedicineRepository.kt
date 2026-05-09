package com.tenko.app.data.model

import android.content.Context
import org.json.JSONArray
import java.text.Normalizer

class MedicineRepository(private val context: Context) {
    private var medicines: List<String> = emptyList()

    init {
        loadMedicines()
    }

    private fun loadMedicines() {
        try {
            val json = context.assets
                .open("medicines.json")
                .bufferedReader()
                .use { it.readText() }

            val jsonArray = JSONArray(json)

            medicines = List(jsonArray.length()) { index ->
                jsonArray.getString(index)
            }
        } catch (e: Exception) {
            medicines = emptyList()
        }
    }

    fun searchMedicines(
        query: String
    ): List<String> {
        if (query.isBlank()) return emptyList()

        return medicines
            .filter {
                normalize(it).contains(
                    normalize(query),
                    ignoreCase = true
                )
            }
            .sorted()
            .take(8)
    }

    private fun normalize(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replace(
            "\\p{InCombiningDiacriticalMarks}+".toRegex(),
            ""
        )
    }
}