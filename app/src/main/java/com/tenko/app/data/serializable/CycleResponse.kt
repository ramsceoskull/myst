package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CycleResponse(
    val id_cycle: Int,

    @Serializable(with = LocalDateSerializer::class)
    val start_date: LocalDate? = null,

    @Serializable(with = LocalDateSerializer::class)
    val end_date: LocalDate? = null,
)