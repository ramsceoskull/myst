package com.tenko.app.data.utils

import com.tenko.app.data.model.CpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

suspend fun getAddressByCP(cp: String): CpResponse? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://postali.app/api/v1/mx/cp/$cp")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.setRequestProperty(
                "Authorization",
                "Bearer pk_live_m6aaavy9_39d5hliazue79yueku3yzyponrwnklnu"
            )

            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)

            val estado = json.getString("estado")
            val municipio = json.getString("municipio")

            val asentamientosJson = json.getJSONArray("asentamientos")
            val asentamientos = List(asentamientosJson.length()) { i ->
                asentamientosJson.getJSONObject(i).getString("nombre")
            }

            CpResponse(estado, municipio, asentamientos)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}