package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ParameterDataPoint(
    @Serializable(with = LocalDateSerializer::class)
    val test_date: LocalDate,

    val value: Double,
    val unit: String? = null,
    val reference_range: String? = null,
    val laboratory_name: String? = null,
    val id_study: Int,
    val trend: Trend = Trend.NONE
)