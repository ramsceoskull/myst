package com.tenko.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tenko.app.data.model.MedicineStatus
import com.tenko.app.data.view.MedicineViewModel
import com.tenko.app.data.view.NotificationViewModel

@Composable
fun FilterSection(viewModel: NotificationViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(
                Color(0xFFEFEFEF),
                RoundedCornerShape(12.dp)
            ),
        horizontalArrangement = Arrangement.Center
    ) {
        TabButton(
            label = "RECIENTES",
            selected = !viewModel.filterUnread,
            onClick = { viewModel.toggleFilter(false) }
        )

        TabButton(
            label = "SIN LEER",
            selected = viewModel.filterUnread,
            onClick = { viewModel.toggleFilter(true) }
        )
    }
}

@Composable
fun FilterSection(viewModel: MedicineViewModel) {
    val selectedFilter by viewModel.filter.collectAsState()

    val options = listOf(
        MedicineStatus.PENDING to "PENDIENTES",
        MedicineStatus.TAKEN to "TOMADAS",
        MedicineStatus.SKIPPED to "SALTADAS"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .background(
                Color(0xFFEFEFEF),
                RoundedCornerShape(12.dp)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        options.forEach { status ->
            TabButton(
                label = status.second,
                selected = selectedFilter == status.first,
                onClick = { viewModel.setFilter(status.first) }
            )
        }
        /*TabButton(
            label = "PENDIENTES",
            selected = selectedFilter == MedicineStatus.PENDING,
            onClick = { viewModel.setFilter(MedicineStatus.PENDING) }
        )
        TabButton(
            label = "TOMADAS",
            selected = selectedFilter == MedicineStatus.TAKEN,
            onClick = { viewModel.setFilter(MedicineStatus.TAKEN) }
        )
        TabButton(
            label = "SALTADAS",
            selected = selectedFilter == MedicineStatus.SKIPPED,
            onClick = { viewModel.setFilter(MedicineStatus.SKIPPED) }
        )*/
    }
}