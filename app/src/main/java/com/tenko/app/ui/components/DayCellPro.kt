package com.tenko.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tenko.app.data.model.CycleEvent
import com.tenko.app.data.model.EventType
import com.tenko.app.ui.theme.NoteColor
import com.tenko.app.ui.theme.OvulationColor
import com.tenko.app.ui.theme.PeriodColor
import com.tenko.app.ui.theme.SelectedDayColor
import com.tenko.app.ui.theme.SymptomColor
import java.time.LocalDate

@Composable
fun DayCellPro(
    day: LocalDate,
    selected: Boolean,
    events: List<CycleEvent>,
    onClick: () -> Unit
) {
    val color = when {
        events.any { it.type == EventType.PERIOD } -> PeriodColor
        events.any { it.type == EventType.OVULATION } -> OvulationColor
        events.any { it.type == EventType.SYMPTOM } -> SymptomColor
        events.any { it.type == EventType.NOTE } -> NoteColor
        else -> Color.Transparent
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    if (selected) SelectedDayColor
                    else color,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                day.dayOfMonth.toString(),
                color = if (selected) Color.White else Color.Black
            )

        }

        if (events.isNotEmpty()) {
            Row {
                repeat(events.size.coerceAtMost(3)) {
                    Box(
                        Modifier
                            .size(5.dp)
                            .background(color, CircleShape)
                    )

                    Spacer(Modifier.width(2.dp))
                }
            }
        }
    }
}