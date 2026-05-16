package com.tenko.app.ui.screen

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.tenko.app.ui.components.CalendarLegendSection
import com.tenko.app.ui.components.FloatingActionButton
import com.tenko.app.ui.components.FloatingLegendSection
import com.tenko.app.ui.components.MotivationalQuoteCard
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

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
                onBackClick = { navController.popBackStack() })
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
                        "future_ovulation" -> Color(0xFF81D4FA).copy(alpha = 0.5f)
                        "future_bleeding" -> Color(0xFFFF6FAE).copy(alpha = 0.5f)
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
    existingLog: DailyLogResponse?, // Pasar el log si ya existe
    onDismiss: () -> Unit,
    onSave: (DailyLogCreate) -> Unit
) {
    var flow by remember { mutableStateOf(existingLog?.menstrual_flow) }
    var mood by remember { mutableStateOf(existingLog?.mood) }
    var weight by remember { mutableStateOf(existingLog?.weight?.toString() ?: "") }
    var notes by remember { mutableStateOf(existingLog?.notes ?: "") }

    // Mapeo de tus Enums de Python
    val flowOptions =
        mapOf(0 to "Nulo", 1 to "Ligero", 2 to "Medio", 3 to "Abundante", 4 to "Goteo")
    val moodOptions =
        mapOf(1 to "Triste", 2 to "Enojada", 3 to "Neutral", 4 to "Feliz", 5 to "Muy feliz")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Registro: $date", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            CatalogChips("Flujo Menstrual", flowOptions, flow) { flow = it }

            CatalogChips("Estado de Ánimo", moodOptions, mood) { mood = it }

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    onSave(
                        DailyLogCreate(
                            date = date,
                            menstrual_flow = flow,
                            mood = mood,
                            weight = weight.toFloatOrNull(),
                            notes = notes.ifBlank { null }
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (existingLog == null) "Guardar Registro" else "Actualizar Registro")
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
            text = when (month.month.name) {
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
            },
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
fun CatalogChips(
    title: String,
    options: Map<Int, String>,
    selectedId: Int?,
    onSelected: (Int) -> Unit
) {
    Column {
        Text(title, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
        FlowRow( // Importa androidx.compose.layout.FlowRow
            modifier = Modifier.fillMaxWidth(),
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


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CalendarScreen(navController: NavHostController) {
//    val currentMonth = remember { YearMonth.now() }
//
//    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
//    var showSheet by remember { mutableStateOf(false) }
//    val events = remember { mutableStateListOf<CalendarEvent>() }
//
//    val state = rememberCalendarState(
//        startMonth = currentMonth.minusMonths(12),
//        endMonth = currentMonth.plusMonths(12),
//        firstVisibleMonth = currentMonth,
//        firstDayOfWeek = DayOfWeek.MONDAY
//    )
//
//    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
//
//    Scaffold(
//        modifier = Modifier
//            .nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            AppTopBar(
//                title = "Calendario",
//                onBackClick = { navController.popBackStack() }
//            )
//        },
//        bottomBar = {
//            BottomNavigationBar(navController)
//        },
//        floatingActionButton = { AddCalendarEvent({}) },
//        floatingActionButtonPosition = FabPosition.End,
//        containerColor = White,
//    ) { padding ->
//        Box (modifier = Modifier.padding(padding)) {
//            Column (
//                modifier = Modifier
//                    .fillMaxWidth().scrollable(state, Orientation.Vertical),
//            ) {
//                CalendarHeader(state)
//
//                HorizontalCalendar(
//                    state = state,
//                    dayContent = { day ->
//                        DayCell(
//                            day = day.date,
//                            selected = day.date == selectedDate,
//                            hasEvent = events.any { it.date == day.date }
//                        ) {
//                            selectedDate = day.date
//                            showSheet = true
//                        }
//
//                    }
//                )
//
//            }
//
//            if (showSheet) {
//                DayBottomSheet(
//                    date = selectedDate,
//                    onDismiss = { showSheet = false },
//                    onSave = { note, symptoms ->
//                        events.removeAll { it.date == selectedDate }
//
//                        events.add(
//                            CalendarEvent(
//                                date = selectedDate,
//                                note = note,
//                                symptoms = symptoms
//                            )
//                        )
//
//                        showSheet = false
//                    }
//                )
//            }
//        }
//    }
//}

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavHostController) {
    val currentMonth = remember { YearMonth.now() }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showSheet by remember { mutableStateOf(false) }
    val events = remember { mutableStateListOf<CycleEvent>() }

    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(24),
        endMonth = currentMonth.plusMonths(24),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(
                title = "Calendario",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = PompAndPower
            ) {
                Icon(
                    painter = painterResource(R.drawable.plus_solid_full),
                    contentDescription = "Registrar síntoma",
                    Modifier.size(24.dp),
                    tint = White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = White,
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column {
                CalendarHeader(state)

                HorizontalCalendar(
                    state = state,
                    dayContent = { day ->

                        val dayEvents = events.filter {
                            it.date == day.date
                        }

                        DayCellPro(
                            day = day.date,
                            selected = day.date == selectedDate,
                            events = dayEvents
                        ) {
                            selectedDate = day.date
                            showSheet = true
                        }
                    }
                )

            }

            if (showSheet) {

                DayEditorSheet(
                    date = selectedDate,
                    onDismiss = { showSheet = false },
                    onSave = { newEvent ->

                        events.removeAll {
                            it.date == newEvent.date &&
                                    it.type == newEvent.type
                        }

                        events.add(newEvent)

                        showSheet = false
                    }
                )
            }
        }
    }
}*/