package com.tenko.app.data.serializable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantResponse(
    val message: String,
    val intent: String,
    val date: String,
    val cycle_id: Int? = null,

    // Recibimos el objeto plano y lo tratamos como ExtractedContent
    @SerialName("data_extracted")
    val data_extracted: ExtractedContent
)