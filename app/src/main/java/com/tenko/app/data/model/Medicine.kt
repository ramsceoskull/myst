package com.tenko.app.data.model

/*data class Medicine(
    val id: Int = 0,
    val name: String = "Omeprazole",
    val description: String = "Used to treat gastroesophageal reflux disease (GERD) and other conditions involving excessive stomach acid.",
    val dosage: String = "20",
    val unit: String = "mg",
    val duration: String = "2 weeks",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val times: List<String> = emptyList(),
    val time: String = "08:00 AM",
    val afterMeal: Boolean = true,
    val showTimeDialog: Boolean = false,
    val status: MedicineStatus = MedicineStatus.PENDING
)*/

data class Medicine(
    val id: Int = 0,
    val name: String = "",
    val description: String = "Used to treat gastroesophageal reflux disease (GERD) and other conditions involving excessive stomach acid.",
    val dosage: String = "",
    val unit: String = "",
    val duration: String = "",
    val startDate: String = "" /*LocalDate? = null*/,
    val endDate: String = "" /*LocalDate? = null*/,
    val times: List<String> = emptyList(),
    val time: String = "",
    val timeFormat: String = "",
    val afterMeal: Boolean = false,
    val showTimeDialog: Boolean = false,
    val status: MedicineStatus = MedicineStatus.PENDING,

    val nameError: String? = null,
    val dateError: String? = null
)