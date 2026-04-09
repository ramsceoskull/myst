package com.tenko.myst.data.serializable

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class ReminderCreate(
    val id_contact: Int? = null,
    val title: String,
    val description: String? = null,

    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,

    @Serializable(with = LocalTimeSerializer::class)
    val time: LocalTime,

    val repeats: String? = null,
    val type: String? = null,
    val priority: String? = null
)