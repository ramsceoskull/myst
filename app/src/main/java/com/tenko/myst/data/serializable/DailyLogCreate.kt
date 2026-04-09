package com.tenko.myst.data.serializable

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class DailyLogCreate(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,

    val weight: Float? = null,
    val height: Float? = null,
    val waist_circumference: Float? = null,

    val systolic_bp: Int? = null,
    val diastolic_bp: Int? = null,
    val heart_rate: Int? = null,

    val body_temperature: Float? = null,
    val glycemia: Float? = null,

    val anticonceptive_use: Boolean? = null,
    val anticonceptive_type: String? = null,

    val sexual_penetration: Boolean? = null,
    val on_fertile_window: Boolean? = null,

    val menstrual_flow: Int? = null,
    val vaginal_discharge: Int? = null,

    val mood: Int? = null,
    val anxiety: Int? = null,
    val stress: Int? = null,

    @Serializable(with = LocalTimeSerializer::class)
    val sleep_time: LocalTime? = null,

    val exercise: String? = null,

    @Serializable(with = LocalTimeSerializer::class)
    val exercise_time: LocalTime? = null,

    val water_consumption: Float? = null,
    val hobbies_activities: String? = null,

    val cramps: Int? = null,
    val cravings: Int? = null,
    val symptoms: String? = null,

    val pregnancy_test: Int? = null,
    val ovulation_test: Int? = null,

    val notes: String? = null
)