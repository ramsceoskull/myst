package com.tenko.app.data.model

data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean,
    val questionRef: ClinicalQuestion? = null
)