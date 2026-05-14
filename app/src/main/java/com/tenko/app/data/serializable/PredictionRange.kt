package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class PredictionRange(
    val predicted_length: Double,

    @Serializable(with = LocalDateSerializer::class)
    val predicted_next_period: LocalDate,

    @Serializable(with = LocalDateSerializer::class)
    val earliest_expected_date: LocalDate,

    @Serializable(with = LocalDateSerializer::class)
    val latest_expected_date: LocalDate
)
