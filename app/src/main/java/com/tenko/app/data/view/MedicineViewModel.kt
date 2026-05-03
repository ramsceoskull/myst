package com.tenko.app.data.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenko.app.data.model.Medicine
import com.tenko.app.data.model.MedicineEvent
import com.tenko.app.data.model.MedicineStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MedicineViewModel : ViewModel() {
    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines
    private var currentId = 0

    val counts: StateFlow<Map<MedicineStatus, Int>> =
        _medicines.map { meds ->
            mapOf(
                MedicineStatus.ALL to meds.size,
                MedicineStatus.TAKEN to meds.count { it.status == MedicineStatus.TAKEN },
                MedicineStatus.SKIPPED to meds.count { it.status == MedicineStatus.SKIPPED },
                MedicineStatus.PENDING to meds.count { it.status == MedicineStatus.PENDING }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _filter = MutableStateFlow(MedicineStatus.PENDING)
    val filter: StateFlow<MedicineStatus> = _filter

    val filteredMedicines: StateFlow<List<Medicine>> =
        combine(_medicines, _filter) { meds, filter ->
            when (filter) {
                MedicineStatus.ALL -> meds
                MedicineStatus.TAKEN -> meds.filter { it.status == MedicineStatus.TAKEN }
                MedicineStatus.SKIPPED -> meds.filter { it.status == MedicineStatus.SKIPPED }
                MedicineStatus.PENDING -> meds.filter { it.status == MedicineStatus.PENDING }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun setFilter(filter: MedicineStatus) {
        _filter.value = filter
    }

    // 🧠 Estado del formulario
    private val _uiState = MutableStateFlow(Medicine())
    val uiState: StateFlow<Medicine> = _uiState

    var state by mutableStateOf(Medicine())
        internal set
    var isSaved by mutableStateOf(false)
        private set

    fun onEvent(event: MedicineEvent) {
        when(event) {
            is MedicineEvent.NameChanged -> { _uiState.update { it.copy(name = event.value) } }
            is MedicineEvent.DoseChanged -> { _uiState.update { it.copy(dosage = event.value) } }
            is MedicineEvent.UnitChanged -> { _uiState.update { it.copy(unit = event.value) } }
            is MedicineEvent.DurationChanged -> { _uiState.update { it.copy(duration = event.value) } }
            is MedicineEvent.ToggleFoodOption -> { _uiState.update { it.copy(afterMeal = !it.afterMeal) } }
            is MedicineEvent.ToggleTimeDialog -> { _uiState.update { it.copy(showTimeDialog = !it.showTimeDialog) } }
            is MedicineEvent.TimeFormatChanged -> { _uiState.update { it.copy(timeFormat = event.value) } }
            is MedicineEvent.StartDateChanged -> { state = state.copy(startDate = event.value) }
            is MedicineEvent.EndDateChanged -> { state = state.copy(endDate = event.value) }
            is MedicineEvent.ReminderTimeChanged -> { state = state.copy(time = event.value) }
            is MedicineEvent.AfterMealChanged -> { state = state.copy(afterMeal = event.value) }
            MedicineEvent.Save -> { validateAndSave() }
        }
    }

    private fun validateAndSave() {
        var valid = true

        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Campo obligatorio") }
            valid = false
        }

        /*if (_uiState.value.startDate.isEmpty() || _uiState.value.endDate.isEmpty()) {
            _uiState.update { it.copy(dateError = "Selecciona fechas validas") }
            valid = false
        }*/

        if (!valid) return

        viewModelScope.launch {
            addMedicine()

            isSaved = true
        }
    }

    fun addMedicine(medicine: Medicine = Medicine()) {
        val form = _uiState.value

        val newMedicine = Medicine(
            id = currentId++,
            name = form.name,
            description = medicine.description,
            dosage = form.dosage,
            unit = form.unit,
            duration = form.duration,
            startDate = form.startDate,
            endDate = form.endDate,
            times = form.times,
            time = form.time,
            timeFormat = form.timeFormat,
            afterMeal = form.afterMeal,
            status = MedicineStatus.PENDING
        )

        /*_medicines.update { current ->
            current + newMedicine
        }*/

        _medicines.value += newMedicine

        _uiState.value = Medicine() // reset form
        viewModelScope.launch {
            delay(2000)
            isSaved = false
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onDosageChange(value: String) {
        _uiState.update { it.copy(dosage = value) }
    }

    fun onUnitChange(value: String) {
        _uiState.update { it.copy(unit = value) }
    }

    fun onDurationChange(value: String) {
        _uiState.update { it.copy(duration = value) }
    }

    fun toggleFoodOption() {
        _uiState.update { it.copy(afterMeal = !it.afterMeal) }
    }

    fun toggleTimeDialog() {
        _uiState.update { it.copy(showTimeDialog = !it.showTimeDialog) }
    }

    fun onTimeFormatChange(value: String) {
        _uiState.update { it.copy(timeFormat = value) }
    }

    fun formatSelectedTime(hour: Int, minute: Int): String {
        val localTime = LocalTime.of(hour, minute)
        // Usamos el formato de 12h o 24h según prefieras, pero asegurando Localidad
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        return localTime.format(formatter)
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        // 1. Formateamos la hora para mostrar en la UI (Local)
        val formattedTime = formatSelectedTime(hour, minute)

        // 2. Determinamos AM/PM
        val format = if (hour >= 12) "pm" else "am"

        _uiState.update {
            it.copy(
                time = formattedTime,
                timeFormat = format,
                showTimeDialog = false // Cerramos el diálogo aquí
            )
        }
    }

//    Prueba de actualización de campos individuales (no recomendado, pero ilustrativo)
    /*fun onNameChange(name: String) {
        _medicines.value = _medicines.value.map {
            it.copy(name = name)
        }
    }

    fun onDosageChange(dosage: String) {
        _medicines.value = _medicines.value.map {
            it.copy(dosage = dosage)
        }
    }

    fun onDurationChange(duration: String) {
        _medicines.value = _medicines.value.map {
            it.copy(duration = duration)
        }
    }

    fun onTimeChange(time: String) {
        _medicines.value = _medicines.value.map {
            it.copy(time = time)
        }
    }

    fun onFoodChange(value: Boolean) {
        _medicines.value = _medicines.value.map {
            it.copy(afterMeal = value)
        }
    }

    fun toggleTimeDialog() {
        _medicines.value = _medicines.value.map {
            it.copy(showTimeDialog = !it.showTimeDialog)
        }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        val formatted = "%02d:%02d".format(hour, minute)
        _medicines.value = _medicines.value.map {
            it.copy(time = formatted, showTimeDialog = false)
        }
    }*/

    /*fun updateName(name: String) {
        _medicines.value = _medicines.value.map {
            it.copy(name = name)
        }
    }

    fun updateDosage(dosage: String, duration: String) {
        _medicines.value = _medicines.value.map {
            it.copy(
                dosage = dosage,
                duration = duration
            )
        }
    }

    fun updateDates(start: LocalDate, end: LocalDate) {
        _medicines.value = _medicines.value.map {
            it.copy(
                startDate = start,
                endDate = end
            )
        }
    }

    fun addTime(time: String) {
        _medicines.value = _medicines.value.map {
            it.copy(
                times = it.times + time
            )
        }
    }

    fun removeTime(time: String) {
        _medicines.value = _medicines.value.map {
            it.copy(
                times = it.times - time
            )
        }
    }

    fun setMealOption(afterMeal: Boolean) {
        _medicines.value = _medicines.value.map {
            it.copy(afterMeal = afterMeal)
        }
    }*/


    fun markAsTaken(medicine: Medicine) {
        updateStatus(medicine, MedicineStatus.TAKEN)
    }

    fun markAsSkipped(medicine: Medicine) {
        updateStatus(medicine, MedicineStatus.SKIPPED)
    }

    fun deleteMedicine(medicine: Medicine) {
        _medicines.value = _medicines.value.filter { it.id != medicine.id }
    }

    fun updateMedicine(updated: Medicine) {
        _medicines.value = _medicines.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    private fun updateStatus(medicine: Medicine, status: MedicineStatus) {
        _medicines.value = _medicines.value.map {
            if (it.id == medicine.id) it.copy(status = status) else it
        }
    }

    /*init {
        generateLargeMockData()
    }

    fun generateLargeMockData() {
        val list = mutableListOf<Medicine>()

        val statuses = listOf(
            MedicineStatus.PENDING,
            MedicineStatus.TAKEN,
            MedicineStatus.SKIPPED,
        )

        for (i in 1..12) {
            list.add(
                Medicine(
                    id = i,
                    name = "Medicamento $i",
                    description = "Dosis ${100 + i}mg",
                    time = "${(6 + i % 12)}:00 ${if (i % 2 == 0) "AM" else "PM"}",
                    afterMeal = i % 2 == 0,
                    status = statuses[i % 3]
                )
            )
        }

        _medicines.value = list
    }*/
}

