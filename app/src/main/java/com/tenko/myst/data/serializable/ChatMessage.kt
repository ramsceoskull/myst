package com.tenko.myst.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val message: String
)