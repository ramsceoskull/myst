package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class DrugResponse(
    val results: List<DrugResult> = emptyList()
)
