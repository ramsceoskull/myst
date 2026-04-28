package com.tenko.app.data.model

import com.tenko.app.R

data class Doctor(
    val id: Int,
    val avatar: Int = R.drawable.doctor0,
    val name: String = "John",
    val lastName: String = "Doe",
    val email: String = "ramsesrame21@gmail.com",
    val about: String = "Dr. John Doe is a highly experienced cardiologist with over 20 years of practice. He specializes in treating heart conditions and has a passion for helping patients improve their cardiovascular health.",
    val speciality: Speciality? = null,
    val genre: Genre? = null,
    val address: Address? = null,
)