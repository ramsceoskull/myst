package com.tenko.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tenko.app.data.view.DoctorViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.AddContactButton
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.BottomNavigationBar
import com.tenko.app.ui.components.DoctorCard
import com.tenko.app.ui.components.ReminderSmallItem
import com.tenko.app.ui.theme.BackgroundColor

@Composable
fun DoctorsScreen(
    navController: NavHostController,
    viewModel: DoctorViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchContacts()
        viewModel.fetchReminders()
    }

    var selectedDoctorName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Mis doctores",
                onBackClick = { navController.popBackStack() },
            )
        },
        floatingActionButton = { AddContactButton(onClick = { navController.navigate(AppScreens.AddDoctorScreen.route) }) },
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            items(viewModel.contacts) { contact ->
                DoctorCard(contact = contact) {
                    selectedDoctorName = "${contact.name} ${contact.last_name}"
                    viewModel.filterRemindersByContact(contact.id_contact)
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }

        if (selectedDoctorName.isNotEmpty()) {
            Spacer(Modifier.height(224.dp))
            HorizontalDivider(Modifier.padding(top = 16.dp))
            Text("Citas con: $selectedDoctorName", style = MaterialTheme.typography.displayLarge, color = Color(0xFF8E44AD))

            if (viewModel.filteredReminders.isEmpty()) {
                Text("No hay recordatorios vinculados a este contacto.", modifier = Modifier.padding(top = 8.dp))
            } else {
                LazyColumn(/*modifier = Modifier.weight(0.8f)*/) {
                    items(viewModel.filteredReminders) { reminder ->
                        ReminderSmallItem(reminder)
                    }
                }
            }
        }
    }
}