package com.tenko.myst.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id_user: Int,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val initials: String? = null,
    val picture: String? = null
)