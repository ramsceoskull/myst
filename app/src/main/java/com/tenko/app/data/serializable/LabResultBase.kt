package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
open class LabResultBase(
    val parameter: String,
    val value: Double,
    val unit: String? = null,
    val reference_range: String? = null
)