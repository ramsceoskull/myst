package com.tenko.app.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.notifications.scheduleAppointmentAlarm
import com.tenko.app.data.serializable.ContactResponse
import com.tenko.app.data.serializable.ReminderCreate
import com.tenko.app.data.view.DoctorViewModel
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.BottomBar
import com.tenko.app.ui.components.DatePickerField
import com.tenko.app.ui.components.FormTextField
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentScreen(
    navController: NavController,
    doctor: ContactResponse,
    viewModel: DoctorViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
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

    val keyboardController = LocalSoftwareKeyboardController.current
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = Instant.now().atZone(ZoneId.systemDefault()).hour,
        initialMinute = Instant.now().atZone(ZoneId.systemDefault()).minute,
        is24Hour = true
    )
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectedDate = Instant
                    .ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
                val today = LocalDate.now(ZoneOffset.UTC)

                return !selectedDate.isBefore(today)
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= LocalDate.now().year
            }
        }
    )
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var title by remember { mutableStateOf("") }
    var reminderDate by remember { mutableStateOf<LocalDate?>(null) }
    var reminderTime by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val hour = timePickerState.hour
    val minute = timePickerState.minute
    var startDate by remember {
        mutableStateOf<LocalDate>(
            Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
        )
    }
    val daysDifference = remember(startDate, reminderDate) {
        if (reminderDate != null) {
            ChronoUnit.DAYS.between(startDate, reminderDate)
        } else {
            null
        }
    }

    val timeIcon = when (hour) {
        in 6..11 -> R.drawable.mug_hot_solid_full
        in 12..17 -> R.drawable.sun_solid_full
        in 18..23 -> R.drawable.moon_solid_full
        else -> R.drawable.bed_solid_full
    }

    val nameFocus = remember { FocusRequester() }
    val dateFocus = remember { FocusRequester() }
    val timeFocus = remember { FocusRequester() }
    val noteFocus = remember { FocusRequester() }

    var nameError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf("") }

    var pendingTriggerAtMillis by remember { mutableStateOf<Long?>(null) }

    fun saveReminder(triggerAtMillis: Long) {
        scheduleAppointmentAlarm(
            context = context,
            triggerAtMillis = triggerAtMillis,
            title = title,
            description = note.ifEmpty { "Tienes una cita programada" }
        )

        viewModel.createContactReminder(
            idContact = doctor.id_contact,
            reminderData = ReminderCreate(
                id_contact = doctor.id_contact,
                title = title,
                description = note,
                start_date = reminderDate!!,
                day_time = reminderTime.takeIf { it.isNotEmpty() }?.let { time ->
                    val parts = time.split(":")
                    if (parts.size == 2) {
                        val hour = parts[0].toIntOrNull() ?: 0
                        val minute = parts[1].toIntOrNull() ?: 0

                        LocalTime.of(hour, minute)
                    } else null
                },
                type = false,
            ),
            context = context,
        )

        viewModel.fetchReminders()
        viewModel.fetchContactReminders(doctor.id_contact)

        Toast.makeText(
            context,
            "Recordatorio guardado correctamente",
            Toast.LENGTH_SHORT
        ).show()

        navController.popBackStack()
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            val trigger = pendingTriggerAtMillis

            if (granted && trigger != null) {
                saveReminder(trigger)
            } else {
                Toast.makeText(
                    context,
                    "No se concedió el permiso para notificaciones",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Agregar recordatorio",
                onBackClick = { navController.popBackStack() }
            ) {}
        },
        bottomBar = {
            BottomBar(
                text = "Guardar recordatorio",
                onClick = {
                    if (title.isEmpty() || reminderDate == null || reminderTime.isEmpty()) {
                        if (title.isEmpty()) nameError = "El asunto del recordatorio es requerido"
                        dateError =
                            if (reminderDate == null) "La fecha de recordatorio es requerida" else ""
                        if (reminderTime.isEmpty()) timeError =
                            "La hora de recordatorio es requerida"

                        Toast.makeText(
                            context,
                            "Por favor completa todos los campos requeridos",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@BottomBar
                    }

                    val timeParts = reminderTime.split(":")
                    val hour = timeParts[0].toInt()
                    val minute = timeParts[1].toInt()

                    val localDateTime = reminderDate!!
                        .atTime(hour, minute)

                    val appointmentMillis = localDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    val triggerAtMillis = appointmentMillis - (2 * 60 * 60 * 1000L)

                    if (triggerAtMillis <= System.currentTimeMillis()) {
                        Toast.makeText(
                            context,
                            "La cita debe programarse con al menos 2 horas de anticipación",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@BottomBar
                    }

                    pendingTriggerAtMillis = triggerAtMillis

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            saveReminder(triggerAtMillis)
                        } else {
                            notificationPermissionLauncher.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        }
                    } else {
                        saveReminder(triggerAtMillis)
                    }
                }
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 25.dp)
                .padding(top = 30.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Configura tu recordatorio",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Agrega los detalles de tu cita para recibir recordatorios personalizados.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            FormTextField(
                type = ContentType.PersonFullName,
                value = title,
                onValueChange = {
                    title = it
                    nameError = null
                },
                label = "Motivo de la cita",
                placeholder = "Consulta con el Dr. Pérez",
                error = nameError,
                focusRequester = nameFocus,
                imeAction = ImeAction.Next,
                scrollState = scrollState,
                scope = scope,
                onNext = {
                    keyboardController?.hide()
                    showDateDialog = true
                },
            )

            Text(
                text = "¿Cuándo necesitas acudir a tu cita?",
                fontSize = 14.sp,
                color = Color.Gray
            )
            DatePickerField(
                label = "Selecciona la fecha",
                value = reminderDate?.format(dateFormatter) ?: "",
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTrailingIconColor = SweetGrey,
                    unfocusedPlaceholderColor = SweetGrey,
                    disabledContainerColor = AntiFlashWhite,
                    disabledBorderColor = Color.Transparent
                ),
                onClick = {
                    dateError = ""
                    showDateDialog = true
                },
            )
            AnimatedVisibility(
                visible = dateError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dateError,
                    modifier = Modifier.padding(start = 6.dp),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                )
            }
            AnimatedVisibility(
                visible = daysDifference != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (daysDifference!! >= 0) PompAndPower else MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Su recordatorio está programado para dentro de: $daysDifference ${if (daysDifference.toInt() == 1) "día" else "días"}",
                        color = White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    )
                }
            }

            Text(
                text = "¿A qué hora es la cita?",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        timeError = ""
                        showTimeDialog = true
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = { reminderTime = it },
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 66.dp),
                    placeholder = { Text("Seleccionar hora") },
                    enabled = false,
                    singleLine = true,
                    colors = colors,
                    trailingIcon = {
                        when (reminderTime.isEmpty()) {
                            true -> Icon(
                                painter = painterResource(R.drawable.arrow_right_solid_full),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            false -> Icon(
                                painter = painterResource(timeIcon),
                                contentDescription = "Icono de tiempo",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(66.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = PompAndPower,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.alarm_clock_solid_full),
                            contentDescription = "Alarm clock icon",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(15.dp)
                        )
                    }
                )
            }
            AnimatedVisibility(
                visible = timeError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = timeError,
                    modifier = Modifier.padding(start = 6.dp),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Notas (Opcional)", fontSize = 14.sp, color = Color.Gray)
            Text(
                "¿Deseas agregar agregar notas adicionales para tu próxima cita?",
                fontSize = 12.sp,
                color = Color.LightGray
            )
            FormTextField(
                type = ContentType.AddressAuxiliaryDetails,
                value = note,
                onValueChange = { note = it },
                label = "",
                placeholder = "Ejemplo: Llevar resultados de laboratorio, preguntar sobre medicamentos, etc.",
                error = null,
                focusRequester = noteFocus,
                imeAction = ImeAction.Done,
                scrollState = scrollState,
                scope = scope,
            )

            Spacer(modifier = Modifier.height(50.dp))
        }

        if (showDateDialog) {
            DatePickerDialog(
                onDismissRequest = { showDateDialog = false },
                dismissButton = {
                    Button(
                        onClick = { showDateDialog = false },
                        content = { Text("Cancelar") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Gray
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDateDialog = false
                            reminderDate = datePickerState.selectedDateMillis?.let { millis ->
                                Instant
                                    .ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                            }
                        },
                        content = { Text("Aceptar") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PompAndPower,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(12.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                },
                shape = RoundedCornerShape(12.dp),
                colors = DatePickerDefaults.colors(containerColor = White),
                content = {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            containerColor = White,
                            titleContentColor = SweetGrey,
                            headlineContentColor = PompAndPower,
                            weekdayContentColor = Color.DarkGray,
                            navigationContentColor = Color.DarkGray,
                            yearContentColor = Color.DarkGray,
                            currentYearContentColor = Tekhelet,
                            selectedYearContentColor = White,
                            disabledSelectedYearContentColor = Color.LightGray,
                            selectedYearContainerColor = PompAndPower,
                            dayContentColor = Color.DarkGray,
                            disabledDayContentColor = Color.LightGray,
                            selectedDayContentColor = White,
                            selectedDayContainerColor = PompAndPower,
                            todayContentColor = Tekhelet,
                            todayDateBorderColor = Tekhelet,
                            dividerColor = SweetGrey,
                        )
                    )
                }
            )
        }

        if (showTimeDialog) {
            AlertDialog(
                onDismissRequest = { showTimeDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            reminderTime =
                                "%02d:%02d".format(timePickerState.hour, timePickerState.minute)
                            showTimeDialog = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = PompAndPower,
                            contentColor = White
                        ),
                        content = { Text("Confirmar") },
                    )
                },
                dismissButton = {
                    TextButton(
                        onClick = { showTimeDialog = false },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Gray
                        ),
                        content = { Text("Cancelar") }
                    )
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.clock_solid_full),
                            contentDescription = "Clock Icon",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            text = "Selecciona la hora",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¿A qué hora necesitas tomar el medicamento?",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        TimePicker(
                            state = timePickerState,
                            colors = TimePickerDefaults.colors(
                                clockDialColor = PompAndPower.copy(alpha = 0.3f),
                                clockDialSelectedContentColor = White,
                                clockDialUnselectedContentColor = RaisinBlack,
                                selectorColor = Tekhelet,
                                containerColor = White,
                                periodSelectorBorderColor = Color.Gray,
                                periodSelectorSelectedContainerColor = Tekhelet,
                                periodSelectorUnselectedContainerColor = Tekhelet,
                                periodSelectorSelectedContentColor = Tekhelet,
                                periodSelectorUnselectedContentColor = Tekhelet,
                                timeSelectorSelectedContainerColor = PompAndPower,
                                timeSelectorUnselectedContainerColor = AntiFlashWhite,
                                timeSelectorSelectedContentColor = White,
                                timeSelectorUnselectedContentColor = RaisinBlack,
                            )
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = White,
                iconContentColor = PompAndPower,
                titleContentColor = PompAndPower,
                textContentColor = Color.Gray,
            )
        }
    }
}