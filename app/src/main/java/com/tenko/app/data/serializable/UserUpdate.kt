package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdate(
    val name: String? = null,
    val initials: String? = null,
    val picture: String? = null
)