package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ClinicalHistoryUpdate(
    val last_name: String? = null,
    val second_last_name: String? = null,

    @Serializable(with = LocalDateSerializer::class)
    val birthdate: LocalDate? = null,

    val sex_legally: String? = null,
    val sex_biology: String? = null,

    val depression_screening: Boolean? = null,
    val depression: Boolean? = null,

    val memory_screening: Boolean? = null,
    val memory_alterations: Boolean? = null,
    val dementia: Boolean? = null,

    val urinary_incontinence_screening: Boolean? = null,
    val urinary_incontinence: Boolean? = null,

    val anemia_screening: Boolean? = null,
    val obesity_screening: Boolean? = null,
    val osteoporosis_screening: Boolean? = null,

    val diabetes_mellitus: String? = null,
    val arterial_hypertension: Boolean? = null,

    @Serializable(with = StringOrListSerializer::class)
    val sustance_use: List<String>? = null,

    @Serializable(with = StringOrListSerializer::class)
    val std: List<String>? = null,

    val turner_syndrome_screening: Boolean? = null,

    val endometriosis_screening: Boolean? = null,
    val endometriosis: Boolean? = null,

    val pcos_screening: Boolean? = null,
    val pcos: Boolean? = null,

    val sexually_active: Boolean? = null,
    val miscarriages_abortions: Int? = null
) {
    init {
        miscarriages_abortions?.let {
            require(it in 0..20) {"Valor de abortos fuera de rango (0-20)"}
        }
    }
}