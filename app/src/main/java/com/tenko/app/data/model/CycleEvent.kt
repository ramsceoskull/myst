package com.tenko.app.data.model

import java.time.LocalDate

data class CycleEvent(
    val date: LocalDate,
    val type: EventType,
    val symptoms: List<String> = emptyList(),
    val note: String = ""
)