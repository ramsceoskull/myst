package com.tenko.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.model.MedicineStatus
import com.tenko.app.data.serializable.ReminderResponse
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.data.view.MedicineViewModel
import com.tenko.app.data.view.NotificationViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.FloatingActionButton
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.BottomNavigationBar
import com.tenko.app.ui.components.EmptyMedicationState
import com.tenko.app.ui.components.FilterSection
import com.tenko.app.ui.components.MedicationCard
import com.tenko.app.ui.components.SuggestionsCard
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    notificationViewModel: NotificationViewModel,
    medicineViewModel: MedicineViewModel = viewModel()
) {
//    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(Unit) {
        medicineViewModel.fetchMedicationReminders()
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val medicines by medicineViewModel.filteredMedicines.collectAsState()
    val medication by medicineViewModel.medicines.collectAsState()
    val currentFilter by medicineViewModel.filter.collectAsState()

//    var showNotifications by remember { mutableStateOf(false) }
    var medicineToDelete by remember { mutableStateOf<ReminderResponse?>(null) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(
                title = "Myst",
                navController = navController,
                scrollBehavior = scrollBehavior,
                notificationViewModel = notificationViewModel,
                authViewModel = authViewModel,
//                actions = { showNotifications = !showNotifications }
            )
        },
        floatingActionButton = {
            FloatingActionButton(R.drawable.red_and_white_pills, false) {
                navController.navigate(AppScreens.AddMedicationScreen.route)
            }
        },
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = BackgroundColor,
    ) { paddingValues ->
        Box {
            LazyColumn(
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
            ) {
                item { SuggestionsCard(navController) }

                item {
                    Text(
                        text = "Control de medicamentos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                if (medication.isEmpty())
                    item { EmptyMedicationState(filter = MedicineStatus.ALL) }
                else {
                    item {
                        FilterSection(viewModel = medicineViewModel)

                        if (medicines.isEmpty())
                            EmptyMedicationState(filter = currentFilter)
                    }

                    items(medicines) { medicine ->
                        MedicationCard(
                            medicine = medicine,
                            onTaken = {
                                medicineViewModel.updateReminderStatus(
                                    medicine.id_reminder,
                                    MedicineStatus.TAKEN.ordinal
                                )
                            },
                            onSkipped = {
                                medicineViewModel.updateReminderStatus(
                                    medicine.id_reminder,
                                    MedicineStatus.SKIPPED.ordinal
                                )
                            },
                            onDelete = { medicineToDelete = medicine }
                            /*onInfo = { navController.navigate("detail/${it.id}") },
                            onEdit = { navController.navigate("edit/${it.id}") },
                            onDelete = { medicineToDelete = it }*/
                        )
                    }
                }
            }

            /*if (showNotifications) {
                NotificationsOverlay(
                    padding = paddingValues.calculateTopPadding(),
                    viewModel = notificationViewModel,
                    onDismiss = { showNotifications = false },
                    onSeeAllClick = { navController.navigate(AppScreens.AllNotificationsScreen.route) },
                    onNotificationClick = { navController.navigate("notification_details_screen/${it.id}") }
                )
            }*/
        }
    }

    if (medicineToDelete != null) {
        AlertDialog(
            onDismissRequest = { medicineToDelete = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        medicineToDelete?.let { medicineViewModel.deleteReminder(it.id_reminder) }
                        medicineViewModel.fetchMedicationReminders()
                        medicineToDelete = null
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = White,
                        containerColor = Tekhelet
                    ),
                    content = { Text("Eliminar") }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { medicineToDelete = null },
                    content = { Text("Cancelar", color = Color.Gray) }
                )
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.trash_solid_full),
                        contentDescription = "Delete Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text("Eliminar medicación")
                }
            },
            text = {
                Text(
                    "La siguiente acción eliminará el recordatorio de medicación \"${medicineToDelete?.title}\".\n¿Deseas continuar?",
                )
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = White,
            titleContentColor = Tekhelet,
            textContentColor = SweetGrey
        )
    }
}