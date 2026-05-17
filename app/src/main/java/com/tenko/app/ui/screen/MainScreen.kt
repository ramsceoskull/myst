package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.model.DialogButton
import com.tenko.app.data.model.DialogTitle
import com.tenko.app.data.model.MedicineStatus
import com.tenko.app.data.notifications.NotificationPermissionHelper
import com.tenko.app.data.serializable.ReminderResponse
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.data.view.MedicineViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.AlertDialog
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.BottomNavigationBar
import com.tenko.app.ui.components.EmptyMedicationState
import com.tenko.app.ui.components.FilterSection
import com.tenko.app.ui.components.FloatingActionButton
import com.tenko.app.ui.components.MedicationCard
import com.tenko.app.ui.components.SuggestionsCard
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    medicineViewModel: MedicineViewModel = viewModel()
) {
    LaunchedEffect(Unit) { medicineViewModel.fetchMedicationReminders() }

    val context = LocalContext.current
    val isRefreshing = medicineViewModel.isLoading
    val medicines by medicineViewModel.filteredMedicines.collectAsState()
    val medication by medicineViewModel.medicines.collectAsState()
    val currentFilter by medicineViewModel.filter.collectAsState()

    var medicineToDelete by remember { mutableStateOf<ReminderResponse?>(null) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "Necesitamos permiso de notificaciones para usar esta función",
                Toast.LENGTH_LONG
            ).show()
        } else
            navController.navigate(AppScreens.AddMedicationScreen.route)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Myst",
                    navController = navController,
                    authViewModel = authViewModel,
                )
            },
            floatingActionButton = {
                FloatingActionButton(R.drawable.red_and_white_pills, false) {
                    if (NotificationPermissionHelper.hasNotificationPermission(context))
                        navController.navigate(AppScreens.AddMedicationScreen.route)
                    else
                        NotificationPermissionHelper
                            .requestNotificationPermission(notificationPermissionLauncher)
                }
            },
            bottomBar = { BottomNavigationBar(navController) },
            containerColor = BackgroundColor,
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { medicineViewModel.fetchMedicationReminders() },
                modifier = Modifier
                    .background(White)
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(start = 20.dp, top = 30.dp, end = 20.dp)
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

                    if (medication.isEmpty() && !medicineViewModel.isLoading)
                        item { EmptyMedicationState(filter = MedicineStatus.ALL) }
                    else {
                        item {
                            FilterSection(viewModel = medicineViewModel)

                            if (medicines.isEmpty() && !medicineViewModel.isLoading)
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
                                /*onEdit = { navController.navigate("edit/${it.id}") }*/
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        }

        if (medicineToDelete != null)
            AlertDialog(
                onDismissRequest = { medicineToDelete = null },
                title = DialogTitle(R.drawable.trash_solid_full, "¿Eliminar medicación?"),
                confirmButton = DialogButton("Sí, eliminar") {
                    medicineToDelete?.let { medicineViewModel.deleteReminder(it.id_reminder) }
                    medicineToDelete = null
                },
                content = {
                    Text(
                        text = "¿Estás seguro de que quieres eliminar el recordatorio para tu " +
                                "medicación \"${medicineToDelete?.title}\"? Esta acción no se puede deshacer."
                    )
                },
            )

        if (isRefreshing) {
            SplashScreen()
        }
    }
}