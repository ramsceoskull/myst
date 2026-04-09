package com.tenko.myst.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class UserCreate(
    val name: String,
    val email: String,
    val password: String,
    val initials: String? = null,
    val picture: String? = null
)