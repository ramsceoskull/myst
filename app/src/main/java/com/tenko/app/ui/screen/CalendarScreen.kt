package com.tenko.app.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.tenko.app.R
import com.tenko.app.data.model.CalendarLegend
import com.tenko.app.data.serializable.DailyLogCreate
import com.tenko.app.data.serializable.DailyLogResponse
import com.tenko.app.data.view.CycleViewModel
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.BottomNavigationBar
import com.tenko.app.ui.components.FloatingActionButton
import com.tenko.app.ui.components.FloatingLegendSection
import com.tenko.app.ui.components.MotivationalQuoteCard
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController, viewModel: CycleViewModel = viewModel()) {
    val legends = listOf(
        CalendarLegend(
            id = "real_bleeding",
            label = "Sangrado registrado",
            color = Color(0xFFFF6FAE),
        ),
        CalendarLegend(
            id = "past_ovulation",
            label = "Ovulación registrada",
            color = Color(0xFF81D4FA),
        ),
        CalendarLegend(
            id = "future_ovulation",
            label = "Ovulación estimada",
            color = Color(0xFF81D4FA).copy(alpha = 0.5f),
        ),
        CalendarLegend(
            id = "future_bleeding",
            label = "Periodo estimado",
            color = Color(0xFFFF6FAE).copy(alpha = 0.5f),
        )
    )

    val currentMonth = remember { YearMonth.now() }
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    var showSheet by remember { mutableStateOf(false) }
    var selectedLegend by remember { mutableStateOf<String?>(null) }
    var showLegend by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val selectedDateLog = viewModel.dailyLogs.find { it.date == selectedDate }

    LaunchedEffect(Unit) { viewModel.fetchData() }

    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth.plusMonths(12),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Calendario",
                onBackClick = { navController.popBackStack() }
            ) {}
        },
        floatingActionButton = {
            FloatingActionButton(R.drawable.note_sticky_solid_full) { showSheet = true }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            MotivationalQuoteCard()

            CalendarHeader(state)

            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    val date = day.date
                    val isFutureDate = date.isAfter(today)
                    val existingLog = viewModel.dailyLogs.find { it.date == date }

                    val eventType = when {
                        existingLog?.menstrual_flow != null && existingLog.menstrual_flow > 0 -> "real_bleeding"

                        viewModel.cycles.any { cycle ->
                            val start = cycle.start_date
                            val end = cycle.end_date

                            if (start != null && end != null) {
                                val totalDays = ChronoUnit.DAYS.between(start, end)

                                date == start.plusDays(totalDays / 2)
                            } else false
                        } -> "past_ovulation"

                        viewModel.prediction?.let { pred ->
                            val startDate =
                                LocalDate.parse(
                                    pred.predicted_cycle_range
                                        .predicted_next_period
                                        .toString()
                                )

                            val cycleLength = pred.predicted_cycle_range.predicted_length
                            val futureOvulationDate = startDate.plusDays((cycleLength / 2).toLong())

                            date == futureOvulationDate
                        } == true -> "future_ovulation"

                        viewModel.prediction?.let { pred ->
                            val startDate =
                                LocalDate.parse(
                                    pred.predicted_cycle_range
                                        .predicted_next_period
                                        .toString()
                                )

                            val length = pred.predicted_cycle_range.predicted_length

                            val bleedingDuration = when {
                                length < 25 -> 4L
                                length <= 32 -> 5L
                                else -> 6L
                            }

                            val endDate = startDate.plusDays(bleedingDuration - 1)

                            !date.isBefore(startDate) && !date.isAfter(endDate)
                        } == true -> "future_bleeding"

                        else -> null
                    }

                    // LÓGICA DE COLORES ACTUALIZADA
                    val baseColor = when (eventType) {
                        "real_bleeding" -> Color(0xFFFF6FAE)
                        "past_ovulation" -> Color(0xFF81D4FA)
                        "future_ovulation" -> Color(0xFF81D4FA)
                        "future_bleeding" -> Color(0xFFFF6FAE)
                        else -> Color.Transparent
                    }

                    val shouldHighlight = selectedLegend == null || selectedLegend == eventType

                    val dayColor = when {
                        baseColor == Color.Transparent -> Color.Transparent
                        shouldHighlight -> baseColor
                        else -> baseColor.copy(alpha = 0.15f)
                    }

                    DayCell(
                        day = date,
                        selected = date == selectedDate,
                        hasEvent = existingLog != null,
                        statusColor = dayColor,
                        isClickable = !isFutureDate, // Bloqueo de días futuros
                        onClick = {
                            if (!isFutureDate) {
                                selectedDate = date
                                showSheet = true
                            }
                        }
                    )
                }
            )

            FloatingLegendSection(
                legends = legends,
                selectedLegend = selectedLegend,
                showLegend = showLegend,
                onToggleLegend = {
                    showLegend = !showLegend
                    scope.launch {
                        if (showLegend) {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        } else {
                            scrollState.animateScrollTo(0)
                        }
                    }
                },
                onLegendSelected = { legendId ->
                    selectedLegend =
                        if (selectedLegend == legendId)
                            null
                        else
                            legendId
                }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        if (showSheet) {
            DailyLogFormSheet(
                date = selectedDate,
                existingLog = selectedDateLog,
                onDismiss = { showSheet = false },
                onSave = { logData ->
                    viewModel.createDailyLog(logData) {
                        showSheet = false
                        viewModel.fetchData()
                    }
                }
            )
        }
    }
}

@Composable
fun DayCell(
    day: LocalDate,
    selected: Boolean,
    hasEvent: Boolean,
    statusColor: Color,
    isClickable: Boolean,
    onClick: () -> Unit
) {
    val today = LocalDate.now()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .alpha(if (isClickable) 1f else 0.3f) // Los días futuros se ven más claros
            .clickable(enabled = isClickable) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (selected) Tekhelet else statusColor,
                    CircleShape
                )
                .then(
                    // Opcional: Borde para el día de hoy
                    if (day == today) Modifier.border(1.dp, Tekhelet, CircleShape) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                day.dayOfMonth.toString(),
                color = if (selected) White else RaisinBlack,
                fontWeight = if (statusColor != Color.Transparent || day == today) FontWeight.Bold else FontWeight.Medium
            )
        }

        if (hasEvent) {
            Box(
                Modifier
                    .padding(top = 2.dp)
                    .size(6.dp)
                    .background(Color.Gray, CircleShape)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLogFormSheet(
    date: LocalDate,
    existingLog: DailyLogResponse?,
    onDismiss: () -> Unit,
    onSave: (DailyLogCreate) -> Unit
) {
    // 1. ESTADOS - CAMPOS DE SELECCIÓN (Chips Enums / Booleans)
    var flow by remember { mutableStateOf<Int?>(null) }
    var discharge by remember { mutableStateOf<Int?>(null) }
    var mood by remember { mutableStateOf<Int?>(null) }
    var anxiety by remember { mutableStateOf<Int?>(null) }
    var stress by remember { mutableStateOf<Int?>(null) }
    var cramps by remember { mutableStateOf<Int?>(null) }
    var cravings by remember { mutableStateOf<Int?>(null) }
    var pregTest by remember { mutableStateOf<Int?>(null) }
    var ovulTest by remember { mutableStateOf<Int?>(null) }

    var sexualPenetration by remember { mutableStateOf(false) }
    var onFertileWindow by remember { mutableStateOf(false) }
    var anticonceptiveType by remember { mutableStateOf("Ninguno") }

    // 2. ESTADOS - MULTISELECCIÓN (Strings)
    var exercise by remember { mutableStateOf("") }
    var hobbies by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }

    // 3. ESTADOS - TIEMPOS (Guardaremos el String formateado provisionalmente "HH:mm")
    var sleepTime by remember { mutableStateOf("") }
    var exerciseTime by remember { mutableStateOf("") }

    // 4. ESTADOS - TEXTFIELDS NUMÉRICOS
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var temp by remember { mutableStateOf("") }
    var glycemia by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }
    var systolicBp by remember { mutableStateOf("") }
    var diastolicBp by remember { mutableStateOf("") }
    var heartRate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // CLAVE PARA ARREGLAR TU BUG: Forzar la actualización de los estados locales cuando cambia el log seleccionado
    LaunchedEffect(existingLog, date) {
        flow = existingLog?.menstrual_flow
        discharge = existingLog?.vaginal_discharge
        mood = existingLog?.mood
        anxiety = existingLog?.anxiety
        stress = existingLog?.stress
        cramps = existingLog?.cramps
        cravings = existingLog?.cravings
        pregTest = existingLog?.pregnancy_test
        ovulTest = existingLog?.ovulation_test

        sexualPenetration = existingLog?.sexual_penetration ?: false
        onFertileWindow = existingLog?.on_fertile_window ?: false
        anticonceptiveType = existingLog?.anticonceptive_type ?: "Ninguno"

        exercise = existingLog?.exercise ?: ""
        hobbies = existingLog?.hobbies_activities ?: ""
        symptoms = existingLog?.symptoms ?: ""

        // Mapeo seguro de LocalTime? a "HH:mm"
        sleepTime = existingLog?.sleep_time?.let {
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                it.hour,
                it.minute
            )
        } ?: ""
        exerciseTime = existingLog?.exercise_time?.let {
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                it.hour,
                it.minute
            )
        } ?: ""

        weight = existingLog?.weight?.toString() ?: ""
        height = existingLog?.height?.toString() ?: ""
        temp = existingLog?.body_temperature?.toString() ?: ""
        glycemia = existingLog?.glycemia?.toString() ?: ""
        water = existingLog?.water_consumption?.toString() ?: ""
        systolicBp = existingLog?.systolic_bp?.toString() ?: ""
        diastolicBp = existingLog?.diastolic_bp?.toString() ?: ""
        heartRate = existingLog?.heart_rate?.toString() ?: ""
        notes = existingLog?.notes ?: ""
    }

    // OPCIONES DE LOS ENUMS
    val flowOptions =
        mapOf(0 to "Nulo", 1 to "Ligero", 2 to "Medio", 3 to "Abundante", 4 to "Goteo")
    val dischargeOptions = mapOf(
        0 to "No sé",
        1 to "Seco",
        2 to "Pegajoso",
        3 to "Cremoso",
        4 to "Acuoso",
        5 to "Clara de huevo",
        6 to "Anormal",
        7 to "Ninguno"
    )
    val moodOptions = mapOf(
        0 to "No sé",
        1 to "Triste",
        2 to "Enojada",
        3 to "Neutral",
        4 to "Feliz",
        5 to "Muy feliz",
        6 to "Cambios de humor"
    )
    val intensityOptions =
        mapOf(0 to "Ninguno", 1 to "Leve", 2 to "Moderado", 3 to "Alto", 4 to "Muy alto")
    val cravingsOptions = mapOf(
        0 to "Todo",
        1 to "Dulce",
        2 to "Salado",
        3 to "Chocolate",
        4 to "Carbohidratos",
        5 to "Comida chatarra",
        6 to "Comida saludable",
        7 to "Picante",
        8 to "Ninguno"
    )
    val testOptions =
        mapOf(0 to "Negativo", 1 to "Positivo", 2 to "Indeterminado", 3 to "No realizada")

    val anticonceptiveList = listOf("Ninguno", "Píldora", "DIU", "Implante", "Inyección", "Condón")
    val exerciseList = listOf(
        "Ninguno",
        "Correr",
        "Nadar",
        "Ciclismo",
        "Senderismo",
        "Yoga",
        "Pesas",
        "Boxeo",
        "Caminar",
        "Otro"
    )
    val hobbiesList = listOf(
        "Ninguna",
        "Lectura",
        "Cuidado personal",
        "Descanso",
        "Baile",
        "Entretenimiento",
        "Pintar",
        "Cocinar",
        "Jardinería",
        "Escribir",
        "Otro"
    )
    val symptomsList = listOf(
        "Dolor de cabeza",
        "Dolor de garganta",
        "Dolor muscular",
        "Dolor de espalda",
        "Falta de aliento",
        "Fatiga",
        "Insomnio",
        "Fiebre",
        "Tos",
        "Hinchazón",
        "Diarrea",
        "Estreñimiento",
        "Náusea o vómito"
    )

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Registro: $date",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // SECCIÓN 1: CHIPS ENUMS DESPLEGABLES
            Text(
                "Ciclo y Síntomas",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            CatalogChips("Flujo Menstrual", flowOptions, flow) { flow = it }
            CatalogChips("Flujo Vaginal", dischargeOptions, discharge) { discharge = it }
            CatalogChips("Estado de Ánimo", moodOptions, mood) { mood = it }
            CatalogChips("Ansiedad", intensityOptions, anxiety) { anxiety = it }
            CatalogChips("Estrés", intensityOptions, stress) { stress = it }
            CatalogChips("Cólicos", intensityOptions, cramps) { cramps = it }
            CatalogChips("Antojos", cravingsOptions, cravings) { cravings = it }
            CatalogChips("Test de Embarazo", testOptions, pregTest) { pregTest = it }
            CatalogChips("Test de Ovulación", testOptions, ovulTest) { ovulTest = it }

            // SECCIÓN 2: MULTISELECCIÓN
            MultiSelectCatalogChips("Síntomas presentados", symptomsList, symptoms) {
                symptoms = it
            }
            MultiSelectCatalogChips("Ejercicios realizados", exerciseList, exercise) {
                exercise = it
            }
            MultiSelectCatalogChips("Hobbies y Actividades", hobbiesList, hobbies) { hobbies = it }

            // SECCIÓN 3: BOOLEANOS Y ANTICONCEPTIVOS
            Text(
                "Actividad y Anticoncepción",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sexualPenetration,
                    onClick = { sexualPenetration = !sexualPenetration },
                    label = { Text("Penetración Sexual") })
                FilterChip(
                    selected = onFertileWindow,
                    onClick = { onFertileWindow = !onFertileWindow },
                    label = { Text("Ventana Fértil") })
            }
            MultiSelectCatalogChips(
                "Método Anticonceptivo",
                anticonceptiveList,
                anticonceptiveType
            ) {
                anticonceptiveType = it.ifBlank { "Ninguno" }
            }

            // SECCIÓN 4: TIME PICKERS
            Text(
                "Registros de Tiempo",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            TimePickerDialogButton("Horas de Sueño", sleepTime) { sleepTime = it }
            TimePickerDialogButton("Duración del Ejercicio", exerciseTime) { exerciseTime = it }

            // SECCIÓN 5: TEXTFIELDS
            Text(
                "Mediciones Clínicas y Métricas",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Altura (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = temp,
                    onValueChange = { temp = it },
                    label = { Text("Temperatura (C°)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = glycemia,
                    onValueChange = { glycemia = it },
                    label = { Text("Glucemia (mg/dL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = systolicBp,
                    onValueChange = { systolicBp = it },
                    label = { Text("Sistólica") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Text("/", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = diastolicBp,
                    onValueChange = { diastolicBp = it },
                    label = { Text("Diastólica") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = heartRate,
                    onValueChange = { heartRate = it },
                    label = { Text("Pulsaciones (lpm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = water,
                    onValueChange = { water = it },
                    label = { Text("Consumo Agua (L)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            // SECCIÓN 6: NOTAS Y ACCIÓN
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas Adicionales") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    // Función auxiliar interna para parsear "HH:mm" seguro a LocalTime
                    val parseTimeToLocalTime: (String) -> java.time.LocalTime? = { timeStr ->
                        timeStr.split(":").takeIf { it.size == 2 }?.let { parts ->
                            val h = parts[0].toIntOrNull()
                            val m = parts[1].toIntOrNull()
                            if (h != null && m != null) java.time.LocalTime.of(h, m) else null
                        }
                    }

                    onSave(
                        DailyLogCreate(
                            date = date,
                            menstrual_flow = flow,
                            vaginal_discharge = discharge,
                            mood = mood,
                            anxiety = anxiety,
                            stress = stress,
                            cramps = cramps,
                            cravings = cravings,
                            pregnancy_test = pregTest,
                            ovulation_test = ovulTest,
                            sexual_penetration = sexualPenetration,
                            on_fertile_window = onFertileWindow,
                            anticonceptive_use = anticonceptiveType != "Ninguno",
                            anticonceptive_type = anticonceptiveType,
                            exercise = exercise.ifBlank { null },
                            hobbies_activities = hobbies.ifBlank { null },
                            symptoms = symptoms.ifBlank { null },
                            sleep_time = parseTimeToLocalTime(sleepTime),
                            exercise_time = parseTimeToLocalTime(exerciseTime),
                            weight = weight.toFloatOrNull(),
                            height = height.toFloatOrNull(),
                            body_temperature = temp.toFloatOrNull(),
                            glycemia = glycemia.toFloatOrNull(),
                            water_consumption = water.toFloatOrNull(),
                            systolic_bp = systolicBp.toIntOrNull(),
                            diastolic_bp = diastolicBp.toIntOrNull(),
                            heart_rate = heartRate.toIntOrNull(),
                            notes = notes.ifBlank { null }
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(if (existingLog == null) "Guardar Registro Completo" else "Actualizar Registro")
            }
        }
    }
}

@Composable
fun CalendarHeader(state: CalendarState) {
    val month = state.firstVisibleMonth.yearMonth

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = when (month.month.name.uppercase()) {
                "JANUARY" -> "Enero"
                "FEBRUARY" -> "Febrero"
                "MARCH" -> "Marzo"
                "APRIL" -> "Abril"
                "MAY" -> "Mayo"
                "JUNE" -> "Junio"
                "JULY" -> "Julio"
                "AUGUST" -> "Agosto"
                "SEPTEMBER" -> "Septiembre"
                "OCTOBER" -> "Octubre"
                "NOVEMBER" -> "Noviembre"
                "DECEMBER" -> "Diciembre"
                else -> month.month.name
            }.uppercase(),
            fontSize = 20.sp,
//            fontWeight = FontWeight.SemiBold
        )

        Text(
            month.year.toString()
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectCatalogChips(
    title: String,
    options: List<String>,
    selectedString: String?,
    onSelectedChanged: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    // Convertimos el string separado por comas en una lista mutable para el estado local
    val currentSelectedList = remember(selectedString) {
        if (selectedString.isNullOrBlank() || selectedString == "Ninguna" || selectedString == "Ninguno") {
            mutableListOf()
        } else {
            selectedString.split(", ").toMutableList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                if (!isExpanded && currentSelectedList.isNotEmpty()) {
                    Text(
                        text = currentSelectedList.joinToString(", "),
                        color = Color(0xFFFF6FAE),
                        fontSize = 14.sp
                    )
                }
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { option ->
                    val isSelected = currentSelectedList.contains(option)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (option == "Ninguno" || option == "Ninguna") {
                                currentSelectedList.clear()
                                currentSelectedList.add(option)
                            } else {
                                currentSelectedList.remove("Ninguno")
                                currentSelectedList.remove("Ninguna")
                                if (isSelected) currentSelectedList.remove(option) else currentSelectedList.add(
                                    option
                                )
                            }

                            val result =
                                if (currentSelectedList.isEmpty()) null else currentSelectedList.joinToString(
                                    ", "
                                )
                            onSelectedChanged(result ?: "")
                        },
                        label = { Text(option) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogButton(
    label: String,
    currentTimeString: String?, // Espera formato "HH:mm"
    onTimeSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val initialHour = currentTimeString?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 0
    val initialMinute = currentTimeString?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Button(onClick = { showDialog = true }) {
            Text(if (currentTimeString.isNullOrBlank()) "Seleccionar" else currentTimeString)
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Selecciona el tiempo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showDialog = false },
                            modifier = Modifier.padding(end = 8.dp)
                        ) { Text("Cancelar") }
                        Button(onClick = {
                            val formattedTime = String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            onTimeSelected(formattedTime)
                            showDialog = false
                        }) { Text("Confirmar") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogChips(
    title: String,
    options: Map<Int, String>,
    selectedId: Int?,
    onSelected: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                if (!isExpanded && selectedId != null) {
                    Text(
                        text = options[selectedId] ?: "",
                        color = Color(0xFFFF6FAE),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { (id, label) ->
                    FilterChip(
                        selected = selectedId == id,
                        onClick = { onSelected(id) },
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}