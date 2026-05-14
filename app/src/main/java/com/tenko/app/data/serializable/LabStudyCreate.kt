package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class LabStudyCreate(
    val laboratory_name: String? = null,

    @Serializable(with = LocalDateSerializer::class)
    val test_date: LocalDate,

    val results: List<LabResultBase>
)