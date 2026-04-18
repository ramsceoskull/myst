package com.tenko.myst.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class ContactResponse(
    val id_contact: Int,
    val name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    val address: String? = null,
    val about: String? = null,
    val specialty: String? = null,
    val genre: Int? = null
)