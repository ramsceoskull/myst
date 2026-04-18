package com.tenko.myst.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.tenko.myst.data.model.CalendarEvent
import com.tenko.myst.ui.components.AddCalendarEvent
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.BottomNavigationBar
import com.tenko.myst.ui.components.DayBottomSheet
import com.tenko.myst.ui.theme.White
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavHostController) {
    val currentMonth = remember { YearMonth.now() }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showSheet by remember { mutableStateOf(false) }
    val events = remember { mutableStateListOf<CalendarEvent>() }

    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth.plusMonths(12),
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
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = { AddCalendarEvent({}) },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = White,
    ) { padding ->
        Box (modifier = Modifier.padding(padding)) {
            Column (
                modifier = Modifier
                    .fillMaxWidth().scrollable(state, Orientation.Vertical),
            ) {
                CalendarHeader(state)

                HorizontalCalendar(
                    state = state,
                    dayContent = { day ->
                        DayCell(
                            day = day.date,
                            selected = day.date == selectedDate,
                            hasEvent = events.any { it.date == day.date }
                        ) {
                            selectedDate = day.date
                            showSheet = true
                        }

                    }
                )

            }

            if (showSheet) {
                DayBottomSheet(
                    date = selectedDate,
                    onDismiss = { showSheet = false },
                    onSave = { note, symptoms ->
                        events.removeAll { it.date == selectedDate }

                        events.add(
                            CalendarEvent(
                                date = selectedDate,
                                note = note,
                                symptoms = symptoms
                            )
                        )

                        showSheet = false
                    }
                )
            }
        }
    }
}

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

@Composable
fun DayCell(
    day: LocalDate,
    selected: Boolean,
    hasEvent: Boolean,
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
                    when {
                        selected -> Color(0xFF7B61FF)
                        else -> Color.Transparent
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                day.dayOfMonth.toString(),
                color = if (selected) Color.White else Color.Black
            )

        }

        if (hasEvent) {
            Box(
                Modifier
                    .size(6.dp)
                    .background(
                        Color(0xFFFF6FAE),
                        CircleShape
                    )
            )
        }

    }
}