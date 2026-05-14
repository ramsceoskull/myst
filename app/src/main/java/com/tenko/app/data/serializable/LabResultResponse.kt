package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class LabResultResponse(
    val id_result: Int,
    val parameter: String,
    val value: Double,
    val unit: String? = null,
    val reference_range: String? = null
)