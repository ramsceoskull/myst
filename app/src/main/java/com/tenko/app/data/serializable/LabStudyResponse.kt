package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class LabStudyResponse(
    val id_study: Int,
    val laboratory_name: String? = null,

    @Serializable(with = LocalDateSerializer::class)
    val test_date: LocalDate,

    @Serializable(with = LocalDateTimeSerializer::class)
    val created_at: LocalDateTime,
    val results: List<LabResultResponse>
)