package com.tenko.app.data.model

sealed class MedicineEvent {
    data class NameChanged(val value: String) : MedicineEvent()

    data class DoseChanged(val value: String) : MedicineEvent()
    data class UnitChanged(val value: String) : MedicineEvent()

    data class DurationChanged(val value: String) : MedicineEvent()
    data class ToggleFoodOption(val value: Boolean) : MedicineEvent()
    data class ToggleTimeDialog(val value: Boolean) : MedicineEvent()
    data class TimeFormatChanged(val value: String) : MedicineEvent()

    data class StartDateChanged(val value: String) : MedicineEvent()

    data class EndDateChanged(val value: String) : MedicineEvent()

    data class ReminderTimeChanged(val value: String) : MedicineEvent()

    data class AfterMealChanged(val value: Boolean) : MedicineEvent()

    object Save : MedicineEvent()
}