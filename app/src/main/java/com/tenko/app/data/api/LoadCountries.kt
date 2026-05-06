package com.tenko.app.data.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tenko.app.data.model.Country

fun loadCountries(context: Context): List<Country> {
    return try {
        val json = context.assets.open("countries.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<Country>>() {}.type
        Gson().fromJson(json, type)
    } catch (e: Exception) {
        emptyList() // evita crash
    }
}