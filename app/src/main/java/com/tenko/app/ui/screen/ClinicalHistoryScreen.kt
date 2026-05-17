package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.model.AnswerType
import com.tenko.app.data.model.ClinicalQuestion
import com.tenko.app.data.serializable.UserUpdate
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.data.view.ChatViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.InfoRow
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import com.tenko.app.data.serializable.ClinicalHistoryUpdate
import com.tenko.app.ui.components.AnswerSelector
import com.tenko.app.ui.components.EmptyClinicalHistoryState
import com.tenko.app.ui.components.EmptyStateFullscreen
import com.tenko.app.ui.components.nameInput
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.SweetGrey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalHistoryScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    viewModel: ChatViewModel = viewModel()
) {
    // Cargar datos al iniciar
    LaunchedEffect(Unit) { viewModel.fetchMyHistory() }

    // Observamos los datos del historial y el estado de carga
    val history by viewModel.historyData.collectAsState()
    val isRefreshing by viewModel.isLoading.collectAsState()
    var showNameInput by remember { mutableStateOf(false) }
    var showLastNameInput by remember { mutableStateOf(false) }
    var showSecondLastNameInput by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showBinaryDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var newName by remember { mutableStateOf("") }
    val initials by remember(newName) {
        derivedStateOf {
            newName
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.first().uppercaseChar() }
                .joinToString("")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Historial Clínico",
                    onBackClick = { navController.popBackStack() }
                ) {}
            }
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel.fetchMyHistory()
                    authViewModel.getUser(navController)
                },
                modifier = Modifier
                    .background(White)
                    .padding(paddingValues)
            ) {
                if (history?.last_name.isNullOrBlank() && !isRefreshing)
                    EmptyClinicalHistoryState(
                        icon = R.drawable.folder_open_solid_full,
                        title = "No hay historial clínico registrado",
                        description = "Agrega tu historial clínico para recibir recomendaciones personalizadas y mejorar tu salud integral.",
                        onClick = { navController.navigate(AppScreens.ChatScreen.route) }
                    )
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
//                SECCIÓN: Identificación
                        SectionTitle("Identificación")

                        InfoRow(
                            label = "Nombre",
                            value = authViewModel.currentUser?.name ?: "No registrado",
                            onClick = { showNameInput = true }
                        )
                        AnimatedVisibility(
                            visible = showNameInput,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                newName =
                                    nameInput(true, authViewModel.currentUser?.name ?: "").first

                                Row {
                                    TextButton(onClick = { showNameInput = false }) {
                                        Text("Cancelar", color = Color.Gray)
                                    }
                                    TextButton(
                                        onClick = {
                                            if (newName.isNotBlank()) {
                                                authViewModel.updateUser(
                                                    updateData = UserUpdate(
                                                        name = newName, initials =
                                                            if (initials.length == 2) initials
                                                            else newName.take(2).uppercase()
                                                    ),
                                                    context = context
                                                ) {
                                                    Toast.makeText(
                                                        context,
                                                        "Nombre actualizado",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    authViewModel.getUser(navController)
                                                }
                                                showNameInput = false
                                            } else
                                                Toast.makeText(
                                                    context,
                                                    "El nombre no puede estar vacío",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = White,
                                            containerColor = Tekhelet
                                        ),
                                        content = { Text("Cambiar nombre") }
                                    )
                                }
                            }
                        }

                        InfoRow(
                            label = "Apellido paterno",
                            value = history?.last_name ?: "No registrado",
                            onClick = { showLastNameInput = true }
                        )
                        AnimatedVisibility(
                            visible = showLastNameInput,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                newName = nameInput(false, history?.last_name ?: "").first

                                Row {
                                    TextButton(onClick = { showLastNameInput = false }) {
                                        Text("Cancelar", color = Color.Gray)
                                    }
                                    TextButton(
                                        onClick = {
                                            if (newName.isNotBlank()) {
                                                scope.launch {
                                                    viewModel.updateSingleField(
                                                        "last_name",
                                                        newName,
                                                        navController
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "Apellido actualizado",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    viewModel.fetchMyHistory()
                                                }
                                                showLastNameInput = false
                                            } else
                                                Toast.makeText(
                                                    context,
                                                    "El apellido no puede estar vacío",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = White,
                                            containerColor = Tekhelet
                                        ),
                                        content = { Text("Cambiar apellido") }
                                    )
                                }
                            }
                        }

                        InfoRow(
                            label = "Apellido materno",
                            value = history?.second_last_name ?: "No registrado",
                            onClick = { showSecondLastNameInput = true }
                        )
                        AnimatedVisibility(
                            visible = showSecondLastNameInput,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                newName = nameInput(false, history?.second_last_name ?: "").first

                                Row {
                                    TextButton(onClick = { showSecondLastNameInput = false }) {
                                        Text("Cancelar", color = Color.Gray)
                                    }
                                    TextButton(
                                        onClick = {
                                            if (newName.isNotBlank()) {
                                                scope.launch {
                                                    viewModel.updateSingleField(
                                                        "second_last_name",
                                                        newName,
                                                        navController
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "Apellido actualizado",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    viewModel.fetchMyHistory()
                                                }
                                                showSecondLastNameInput = false
                                            } else
                                                Toast.makeText(
                                                    context,
                                                    "El apellido no puede estar vacío",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = White,
                                            containerColor = Tekhelet
                                        ),
                                        content = { Text("Cambiar apellido") }
                                    )
                                }
                            }
                        }

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        var birthDate by remember { mutableStateOf<LocalDate?>(null) }

                        val zoneId = ZoneId.systemDefault()
                        val today = LocalDate.now()
                        val state = rememberDatePickerState(
                            selectableDates = object : SelectableDates {
                                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                    val selectedDate = Instant
                                        .ofEpochMilli(utcTimeMillis)
                                        .atZone(zoneId)
                                        .toLocalDate()
                                    return selectedDate.isBefore(today)
                                }

                                override fun isSelectableYear(year: Int): Boolean {
                                    return year <= today.year
                                }
                            }
                        )

                        InfoRow(
                            label = "Fecha de nacimiento",
                            /*value = history?.birthdate.toString().let {
                                "${history?.birthdate?.dayOfMonth.toString().padStart(2, '0')}-${
                                    history?.birthdate?.monthValue.toString().padStart(2, '0')
                                }-${history?.birthdate?.year}"
                            },*/
                            value = if (history?.birthdate != null) {
                                try {
                                    LocalDate.parse(history!!.birthdate.toString(), formatter)
                                        .format(formatter)
                                } catch (e: Exception) {
                                    "Formato no válido"
                                }
                            } else "No registrado",
                            onClick = { showDateDialog = true }
                        )
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
                                            scope.launch {
                                                birthDate =
                                                    state.selectedDateMillis?.let { millis ->
                                                        LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                                                    }
                                                viewModel.updateSingleField(
                                                    "birthdate",
                                                    birthDate?.format(formatter) ?: "",
                                                    navController
                                                )
                                                Toast.makeText(
                                                    context,
                                                    "Fecha de nacimiento actualizada",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                delay(2000)
                                            }

                                            viewModel.fetchMyHistory()
                                            showDateDialog = false
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
                                        state = state,
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

                        InfoRow(
                            label = "Sexo biológico",
                            value = when (history?.sex_biology) {
                                "femenine" -> "Femenino"
                                "masculine" -> "Masculino"
                                else -> "No registrado"
                            },
                            onClick = { showBinaryDialog = true }
                        )
                        AnimatedVisibility(
                            visible = showBinaryDialog,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                AnswerSelector(
                                    ClinicalQuestion(
                                        "",
                                        "",
                                        AnswerType.SingleChoice(
                                            options = mapOf(
                                                "femenine" to "Femenino",
                                                "masculine" to "Masculino"
                                            )
                                        )
                                    ), { answer ->
                                        viewModel.updateSingleField(
                                            "sex_biology",
                                            when (answer) {
                                                "Femenino" -> "femenine"
                                                "Masculino" -> "masculine"
                                                else -> null
                                            },
                                            navController
                                        )
                                        Toast.makeText(
                                            context,
                                            "Sexo biológico actualizado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showBinaryDialog = false
                                    }
                                )
                                TextButton(onClick = { showBinaryDialog = false }) {
                                    Text("Cancelar", color = Color.Gray)
                                }
                            }
                        }

//                SECCIÓN: Identidad y Biología
                        SectionTitle("Información General")

                        InfoRow(
                            label = "Sexo legal",
                            value = when (history?.sex_legally) {
                                "femenine" -> "Femenino"
                                "masculine" -> "Masculino"
                                else -> "No registrado"
                            },
                            onClick = { /* Diálogo de selección */ }
                        )

                        InfoRow(
                            label = "¿Es activ@ sexualmente?",
                            value = when (history?.sexually_active) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "sexually_active",
                                    !(history?.sexually_active ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "¿Ha tenido abortos?",
                            value = when (history?.miscarriages_abortions) {
                                null -> "No registrado"
                                0 -> "No"
                                else -> "${history?.miscarriages_abortions}"
                            },
                            onClick = { /* Diálogo para ingresar número */ }
                        )

//                SECCIÓN: Condiciones Médicas
                        SectionTitle("Antecedentes clínicos")

                        InfoRow(
                            label = "¿Ha sido diagnosticad@ con diabetes?",
                            value = when (history?.diabetes_mellitus) {
                                "none" -> "Ninguna"
                                "type_1" -> "Tipo 1"
                                "type_2" -> "Tipo 2"
                                "gestational" -> "Gestacional"
                                "prediabetes" -> "Prediabetes"
                                else -> "No registrado"
                            },
                            onClick = { /* Diálogo de opciones */ }
                        )

                        InfoRow(
                            label = "¿Tiene presión alta (hipertensión)?",
                            value = when (history?.arterial_hypertension) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "arterial_hypertension",
                                    !(history?.arterial_hypertension ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "¿Ha tenido o tiene algún diagnóstico de depresión?",
                            value = when (history?.depression) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "depression",
                                    !(history?.depression ?: false),
                                    navController
                                )
                            }
                        )

                        // Ejemplo de edición directa con la función dinámica que creamos
                        InfoRow(
                            label = "¿Le han diagnosticado síndrome de ovario poliquístico (PCOS)?",
                            value = when (history?.pcos) {
                                true -> "Sí"
                                false -> "No"
                                else -> "No registrado"
                            },
                            onClick = {
                                // Diálogo rápido de cambio
                                viewModel.updateSingleField(
                                    "pcos",
                                    !(history?.pcos ?: false),
                                    navController
                                )
                                Toast.makeText(context, "Dato actualizado", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )

                        InfoRow(
                            label = "¿Tiene endometriosis?",
                            value = when (history?.endometriosis) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "endometriosis",
                                    !(history?.endometriosis ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "¿Ha tenido alguna infección o enfermedad de transmisión sexual (ETS)?",
                            value = if (history?.std.isNullOrEmpty()) "No registrado" else if (history?.std == listOf(
                                    "none"
                                )
                            ) "No" else "Sí",
                            onClick = { /* Diálogo de selección múltiple */ }
                        )

                        InfoRow(
                            label = "¿Presenta alteraciones de memoria?",
                            value = when (history?.memory_alterations) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "memory_alterations",
                                    !(history?.memory_alterations ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "¿Tiene diagnóstico o riesgo de demencia?",
                            value = when (history?.dementia) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "dementia",
                                    !(history?.dementia ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "¿Padece incontinencia urinaria?",
                            value = when (history?.urinary_incontinence) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "urinary_incontinence",
                                    !(history?.urinary_incontinence ?: false),
                                    navController
                                )
                            }
                        )

//                SECCIÓN: Hábitos
                        SectionTitle("Hábitos de consumo")

                        InfoRow(
                            label = "Sustancias",
                            value =
                                if (history?.sustance_use.isNullOrEmpty()) "No registrado"
                                else if (history?.sustance_use == listOf("none")) "No" else "Sí",
                            onClick = { /* Diálogo de selección múltiple */ }
                        )

//                SECCIÓN: Ciclo menstrual
                        SectionTitle("Información del ciclo menstrual")

                        InfoRow(
                            label = "Promedio ciclo (días)",
                            value = history?.average_menstrual_cycle?.toString() ?: "No registrado",
                            onClick = { /* Diálogo para ingresar número */ }
                        )

                        InfoRow(
                            label = "¿Tiene ciclos menstruales regulares?",
                            value = history?.regularity ?: "No registrado",
                            onClick = {
                            }
                        )

                        InfoRow(
                            label = "Ciclo actual",
                            value = history?.last_period_date?.toString() ?: "No registrado",
                            onClick = { /* Diálogo para ingresar número */ }
                        )

//                SECCIÓN: Riesgos y Screenings
                        SectionTitle("Mis señales de alerta (screening)")

                        InfoRow(
                            label = "Depresión",
                            value = when (history?.depression_screening) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "depression_screening",
                                    !(history?.depression_screening ?: false),
                                    navController
                                )
                                Toast.makeText(context, "Dato actualizado", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )

                        InfoRow(
                            label = "Alteraciones de memoria",
                            value = when (history?.memory_screening) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "memory_screening",
                                    !(history?.memory_screening ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "Incontinencia urinaria",
                            value = when (history?.urinary_incontinence_screening) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "urinary_incontinence_screening",
                                    !(history?.urinary_incontinence_screening ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "Anemia",
                            value = when (history?.anemia_screening) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "anemia_screening",
                                    !(history?.anemia_screening ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "Obesidad",
                            value = when (history?.obesity_screening) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "obesity_screening",
                                    !(history?.obesity_screening ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "Osteoporosis",
                            value = when (history?.osteoporosis_screening) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "osteoporosis_screening",
                                    !(history?.osteoporosis_screening ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "Sindrome de Turner",
                            value = when (history?.turner_syndrome_screening) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "turner_syndrome_screening",
                                    !(history?.turner_syndrome_screening ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "Endometriosis",
                            value = when (history?.endometriosis_screening) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "endometriosis_screening",
                                    !(history?.endometriosis_screening ?: false),
                                    navController
                                )
                            }
                        )

                        InfoRow(
                            label = "Síndrome de Ovario Poliquístico",
                            value = when (history?.pcos_screening) {
                                null -> "No registrado"
                                true -> "Sí"
                                false -> "No"
                            },
                            onClick = {
                                viewModel.updateSingleField(
                                    "pcos_screening",
                                    !(history?.pcos_screening ?: false),
                                    navController
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }

        if (isRefreshing) {
            SplashScreen()
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = Tekhelet
    )
}