package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class ReminderCreate(
    val id_contact: Int? = null,
    val title: String,
    val description: String? = null,

    @Serializable(with = LocalDateSerializer::class)
    val start_date: LocalDate,

    @Serializable(with = LocalDateSerializer::class)
    val end_date: LocalDate? = null,

    @Serializable(with = LocalTimeSerializer::class)
    val day_time: LocalTime? = null,

    val type: Boolean? = null,
    val dosage: String? = null,
    val after_meal: Boolean = false,
    val status: Int? = null
)