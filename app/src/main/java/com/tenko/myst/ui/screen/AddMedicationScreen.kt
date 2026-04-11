package com.tenko.myst.ui.screen

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.myst.R
import com.tenko.myst.data.model.MedicineEvent
import com.tenko.myst.data.notifications.MedicationReceiver
import com.tenko.myst.data.view.MedicineViewModel
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.DatePickerField
import com.tenko.myst.ui.components.DropdownField
import com.tenko.myst.ui.components.FoodOption
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.White
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(viewModel: MedicineViewModel, onClose: () -> Unit = {}) {
    val state = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var reminderTime by remember { mutableStateOf("") }
    val hour = reminderTime.split(":").getOrNull(0)?.toIntOrNull() ?: 0
    val minute = reminderTime.split(":").getOrNull(1)?.toIntOrNull() ?: 0
    var timeFormat by remember { mutableStateOf("") }
    val daysDifference = remember(startDate, endDate) {
        if (startDate != null && endDate != null) {
            ChronoUnit.DAYS.between(startDate, endDate)
        } else {
            null
        }
    }

    var showSuccess by remember { mutableStateOf(false) }

    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = White,
        unfocusedContainerColor = AntiFlashWhite,
        focusedBorderColor = PompAndPower,
        unfocusedBorderColor = Color.Transparent,
        focusedTrailingIconColor = PompAndPower,
        unfocusedTrailingIconColor = SweetGrey,
        unfocusedPlaceholderColor = Color.Gray,
        disabledContainerColor = AntiFlashWhite,
        disabledBorderColor = Color.Transparent
    )
    LaunchedEffect(viewModel.isSaved) {
        if (viewModel.isSaved) {
            showSuccess = true
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Registrar medicamento",
                onBackClick = onClose,
                navigationIcon = R.drawable.xmark_solid_full
            )
        },
        bottomBar = {
            Spacer(modifier = Modifier.height(8.dp))
        },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding).padding(16.dp)
        ) {
            // Nombre
            Text("Nombre del medicamento", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.value.name,
                onValueChange = { viewModel.onNameChange(it) },
                placeholder = { Text("Buscar medicamento") },
                isError = state.value.nameError != null,
                supportingText = { state.value.nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.magnifying_glass_solid_full),
                        contentDescription = "Icono de búsqueda",
                        modifier = Modifier.size(20.dp)
                    )
                },
                colors = colors,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dosage & Duration
            Text("Dosis y duración", fontSize = 14.sp, color = Color.Gray)
            Text(
                text = "¿Cuánto medicamento ocupas aplicar? & ¿De qué tipo es?",
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val plural = state.value.dosage > "1"
                OutlinedTextField(
                    value = state.value.dosage,
                    onValueChange = { newValue ->
                        if(newValue.all { it.isDigit() }) {
                            viewModel.onDosageChange(newValue)
                        }
                    },
                    placeholder = { Text("Digite cantidad") },
                    shape = RoundedCornerShape(12.dp),
                    colors = colors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                DropdownField(
                    options = if(plural) listOf("Píldoras", "Inyecciones", "Mg", "Ml") else listOf("Píldora", "Inyección", "Mg", "Ml"),
                    selected = state.value.unit.ifEmpty { "Tipo" },
                    onSelected = { viewModel.onUnitChange(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Fecha", fontSize = 14.sp, color = Color.Gray)
            Text("Selecciona el plazo de medicación", fontSize = 12.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(6.dp))
            // --- SECCIÓN FECHA INICIO ---
            DatePickerField("Fecha de inicio", startDate?.format(formatter) ?: "", colors) {
                showDatePicker(context) { date -> startDate = date }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // --- SECCIÓN FECHA FIN ---
            DatePickerField("Fecha de fin", endDate?.format(formatter) ?: "", colors) {
                showDatePicker(context) { date -> endDate = date }
            }

            Spacer(modifier = Modifier.height(8.dp))

            daysDifference?.let { days ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if(days >= 0) PompAndPower else MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if(days > 0) "Su tratamiento durará: $days ${if(days.toInt() == 1) "día" else "días"}"
                            else "Error: Fecha de fin debe ser posterior a fecha de inicio",
                        color = White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(12.dp).fillMaxWidth()
                    )
                    viewModel.onDurationChange(days.toString())
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            // Time
            Text("Hora", fontSize = 14.sp, color = Color.Gray)
            Text("Selecciona la hora que ocupas tomar el medicamento", fontSize = 12.sp, color = Color.LightGray)

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val hourLabel = when {
                    hour == 0 -> viewModel.onTimeFormatChange("AM/PM")
                    hour >= 12 -> viewModel.onTimeFormatChange("PM")
                    else -> viewModel.onTimeFormatChange("AM")
                }
                val timeIcon = when (hour) {
                    in 6..11 -> R.drawable.mug_hot_solid_full
                    in 12..17 -> R.drawable.sun_solid_full
                    in 18..23 -> R.drawable.moon_solid_full
                    else -> R.drawable.bed_solid_full
                }

                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = { viewModel.onTimeSelected(hour, it) },
                    placeholder = { Text("Seleccionar hora") },
                    readOnly = true,
                    colors = colors,
                    trailingIcon = {
                        if(reminderTime.isNotEmpty())
                            Icon(
                                painter = painterResource(timeIcon),
                                contentDescription = "Icono de tiempo",
                                modifier = Modifier.size(20.dp)
                            )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        val timePicker = TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                reminderTime = "%02d:%02d".format(hour, minute)
                            },
                            12, 0, true
                        )
                        timePicker.show()
                    },
                    modifier = Modifier.size(52.dp).background(color = PompAndPower, shape = RoundedCornerShape(12.dp)),
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.clock_solid_full),
                            contentDescription = "Icono de reloj",
                            tint = White,
                            modifier = Modifier.size(25.dp)
                        )
                        Icon(
                            painter = painterResource(R.drawable.plus_solid_full),
                            contentDescription = "Icono de agregar",
                            tint = White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Food & Pill
            Text("Alimentación", fontSize = 14.sp, color = Color.Gray)
            Text("¿Cuándo necesitas tomar el medicamento?", fontSize = 12.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FoodOption(
                    text = "Antes de comer",
                    selected = !state.value.afterMeal,
                    onClick = { viewModel.toggleFoodOption() },
                )
                FoodOption(
                    text = "Después de comer",
                    selected = state.value.afterMeal,
                    onClick = { viewModel.toggleFoodOption() },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animación éxito
            AnimatedVisibility(
                visible = showSuccess,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.check_solid_full),
                        contentDescription = "Guardado exitoso",
                        tint = Color.Green,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Guardado correctamente", color = Color.Green)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Row(modifier = Modifier.height(40.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    content = { Text(text = "Schedule") }
                )

                OutlinedButton(
                    onClick = {
                        viewModel.onEvent(MedicineEvent.Save)
                        /*if (name.isNotEmpty()) {
                            viewModel.addMedicine(
                                Medicine(
//                                    startDate = startDate!!,
//                                    endDate = endDate!!,
                                    time = reminderTime,
                                    timeFormat = timeFormat
                                )
                            )
                        }*/
                        scheduleMedicationAlarm(context, startDate, endDate, hour, minute)
                        Toast.makeText(context, "Recordatorio programado", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = PompAndPower,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    content = { Text(text = "Guardar") }
                )
            }
        }
    }
}

fun showDatePicker(context: Context, onDateSelected: (LocalDate) -> Unit) {
    val now = LocalDate.now()
    android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        now.year, now.monthValue - 1, now.dayOfMonth
    ).show()
}

fun scheduleMedicationAlarm(context: Context, startDate: LocalDate?, endDate: LocalDate?, hour: Int = 8, minute: Int = 0) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MedicationReceiver::class.java).apply {
        putExtra("title", "Hora de tu medicamento")
        putExtra("endDate", endDate.toString())
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Ejemplo: Programar para mañana a las 8:00 AM
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        // Si hoy es después de la fecha de inicio, empezar mañana
        // Si no, empezar en la fecha de inicio
    }

    // Alarmas exactas (requieren permiso SCHEDULE_EXACT_ALARM en Android 12+)
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY, // Se repite cada día
        pendingIntent
    )
}
