package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class ParameterEvolutionResponse(
    val parameter: String,
    val data_points: List<ParameterDataPoint>
)