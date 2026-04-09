package com.tenko.myst.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class ContactCreate(
    val name: String,
    val last_name: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    val address: String? = null
)