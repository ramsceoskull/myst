package com.tenko.myst.data.serializable

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class ReminderResponse(
    val id_reminder: Int? = null,

    val id_user: Int? = null,
    val id_contact: Int? = null,

    val title: String? = null,
    val description: String? = null,

    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate? = null,

    @Serializable(with = LocalTimeSerializer::class)
    val time: LocalTime? = null,

    val repeats: String? = null,
    val type: String? = null,
    val priority: String? = null,

    val is_completed: String? = null
)