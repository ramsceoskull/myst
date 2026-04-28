package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class AssistantResponse(
    val message: String,
    val intent: String,
    val date: String,
    val data_extracted: Map<String, kotlinx.serialization.json.JsonElement>
)
