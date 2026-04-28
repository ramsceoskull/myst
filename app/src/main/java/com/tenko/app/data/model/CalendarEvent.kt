package com.tenko.app.data.model

import java.time.LocalDate

data class CalendarEvent (
    val date: LocalDate,
    val note: String = "",
    val symptoms: List<String> = emptyList(),
    val medications: List<String> = emptyList(),
    val mood: String = "",
    val energyLevel: Int = 0,
    val sleepHours: Double = 0.0
)