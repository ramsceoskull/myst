package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ExtractedContent(
    val is_red_flag: Boolean = false,
    val response: String, // El mensaje empático
    // Capturamos el resto de los campos (mood, symptoms, etc.) en un Map dinámico
    val data: Map<String, JsonElement> = emptyMap()
)
