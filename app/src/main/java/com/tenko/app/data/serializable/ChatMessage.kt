package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val message: String
)