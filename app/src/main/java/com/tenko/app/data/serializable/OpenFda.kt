package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class OpenFda(
    val brand_name: List<String>? = null
)