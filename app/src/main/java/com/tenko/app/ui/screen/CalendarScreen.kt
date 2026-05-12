package com.tenko.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.tenko.app.data.serializable.DailyLogCreate
import com.tenko.app.data.serializable.DailyLogResponse
import com.tenko.app.data.view.CycleViewModel
import com.tenko.app.ui.components.AddCalendarEvent
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.BottomNavigationBar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController, viewModel: CycleViewModel = viewModel()) {
    val currentMonth = remember { YearMonth.now() }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showSheet by remember { mutableStateOf(false) }

    // Obtenemos el log de la fecha seleccionada para pasarlo al BottomSheet
    val selectedDateLog = viewModel.dailyLogs.find { it.date == selectedDate }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) { viewModel.fetchData() }

    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth.plusMonths(12),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    Scaffold(
        topBar = { AppTopBar(title = "Calendario", onBackClick = { navController.popBackStack() }) },
        floatingActionButton = { AddCalendarEvent({ showSheet = true }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CalendarHeader(state)

            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    val date = day.date
                    val existingLog = viewModel.dailyLogs.find { it.date == date }

                    // LÓGICA DE COLORES MEJORADA
                    val dayColor = when {
                        // 1. Sangrado Real (Flujo > 0) -> Rosa Fuerte
                        existingLog?.menstrual_flow != null && existingLog.menstrual_flow > 0 -> Color(0xFFFF6FAE)

                        // 2. Ovulación Estimada (Aprox. día 14 de un ciclo de 28) -> Azul
                        viewModel.cycles.any { cycle ->
                            val ovulationDate = cycle.start_date?.plusDays(14)
                            date == ovulationDate
                        } -> Color(0xFF81D4FA)

                        // 3. Días del ciclo actual (Sin sangrado reportado aún) -> Rosa Tenue
                        viewModel.cycles.any { cycle ->
                            val end = cycle.end_date ?: LocalDate.now()
                            !date.isBefore(cycle.start_date) && !date.isAfter(end)
                        } -> Color(0xFFFF6FAE).copy(alpha = 0.3f)

                        // 4. Predicción Futura -> Rojo suave
                        viewModel.prediction?.let { pred ->
                            val start = LocalDate.parse(pred.predicted_cycle_range.start)
                            val end = LocalDate.parse(pred.predicted_cycle_range.end)
                            !date.isBefore(start) && !date.isAfter(end)
                        } == true -> Color.Red.copy(alpha = 0.2f)

                        else -> Color.Transparent
                    }

                    DayCell(
                        day = date,
                        selected = date == selectedDate,
                        hasEvent = existingLog != null,
                        statusColor = dayColor,
                        onClick = {
                            selectedDate = date
                            showSheet = true
                        }
                    )
                }
            )
        }

        // UN SOLO SHEET con la data vinculada correctamente
        if (showSheet) {
            DailyLogFormSheet(
                date = selectedDate,
                existingLog = selectedDateLog,
                onDismiss = { showSheet = false },
                onSave = { logData ->
                    viewModel.createDailyLog(logData) {
                        showSheet = false
                        viewModel.fetchData() // Refrescar tras guardar
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
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (selected) Color(0xFF7B61FF) else statusColor,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                day.dayOfMonth.toString(),
                color = if (selected) Color.White else Color.Black,
                fontWeight = if (statusColor != Color.Transparent) FontWeight.Bold else FontWeight.Normal
            )
        }

        if (hasEvent) {
            Box(
                Modifier
                    .padding(top = 2.dp)
                    .size(6.dp)
                    .background(Color(0xFFFF6FAE), CircleShape)
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
    val flowOptions = mapOf(0 to "Nulo", 1 to "Ligero", 2 to "Medio", 3 to "Abundante", 4 to "Goteo")
    val moodOptions = mapOf(1 to "Triste", 2 to "Enojada", 3 to "Neutral", 4 to "Feliz", 5 to "Muy feliz")

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
                    onSave(DailyLogCreate(
                        date = date,
                        menstrual_flow = flow,
                        mood = mood,
                        weight = weight.toFloatOrNull(),
                        notes = notes.ifBlank { null }
                    ))
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
            month.month.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
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