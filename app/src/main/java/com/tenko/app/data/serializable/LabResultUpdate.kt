package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class LabResultUpdate(
    val parameter: String? = null,
    val value: Double? = null,
    val unit: String? = null,
    val reference_range: String? = null
)