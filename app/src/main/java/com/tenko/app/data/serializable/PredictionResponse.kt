package com.tenko.app.data.serializable
import kotlinx.serialization.Serializable

@Serializable
data class PredictionResponse(
    val predicted_cycle_length: Double,
    val predicted_next_period: String, // La fecha viene como "YYYY-MM-DD"
    val predicted_cycle_range: PredictionRange
)
