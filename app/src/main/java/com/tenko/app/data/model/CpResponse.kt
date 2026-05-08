package com.tenko.app.data.model

data class CpResponse(
    val estado: String,
    val municipio: String,
    val asentamientos: List<String>
)
