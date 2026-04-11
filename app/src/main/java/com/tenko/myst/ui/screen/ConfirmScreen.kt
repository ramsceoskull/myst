package com.tenko.myst.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController

/*
@Composable
fun ConfirmScreen(
    navController: NavController,
    viewModel: FormStateViewModel
) {

    val state by viewModel.formState.collectAsState()

    Column {
        Text("Resumen")
        Text("Nombre: ${state.name}")
        Text("Dosis: ${state.dose}")
        Text("Fechas: ${state.startDate} - ${state.endDate}")
        Text("Horas: ${state.reminderTime}")

        Button(onClick = {
            // guardar en lista principal
        }) {
            Text("Guardar medicamento")
        }
    }
}*/
