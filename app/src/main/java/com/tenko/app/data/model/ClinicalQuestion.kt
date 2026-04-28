package com.tenko.app.data.model

data class ClinicalQuestion (
    val id: String, // Coincide con el campo del JSON de la API

    val label: String,

    val type: AnswerType
)