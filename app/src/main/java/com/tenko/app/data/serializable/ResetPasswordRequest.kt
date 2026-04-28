package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val new_password: String
)