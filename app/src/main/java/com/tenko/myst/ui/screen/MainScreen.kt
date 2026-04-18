package com.tenko.myst.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.tenko.myst.R
import com.tenko.myst.data.model.Medicine
import com.tenko.myst.data.model.MedicineStatus
import com.tenko.myst.data.view.AuthViewModel
import com.tenko.myst.data.view.MedicineViewModel
import com.tenko.myst.data.view.NotificationViewModel
import com.tenko.myst.data.view.ProfilePictureViewModel
import com.tenko.myst.navigation.AppScreens
import com.tenko.myst.ui.components.AddMedicationButton
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.BottomNavigationBar
import com.tenko.myst.ui.components.EmptyMedicationState
import com.tenko.myst.ui.components.FilterSection
import com.tenko.myst.ui.components.MedicationCard
import com.tenko.myst.ui.components.NotificationsOverlay
import com.tenko.myst.ui.components.SuggestionsCard
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    notificationViewModel: NotificationViewModel,
    medicineViewModel: MedicineViewModel,
    authViewModel: AuthViewModel = viewModel()
) {
//    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showNotifications by remember { mutableStateOf(false) }

    var medicineToDelete by remember { mutableStateOf<Medicine?>(null) }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(
                title = "Myst",
                navController = navController,
                scrollBehavior = scrollBehavior,
                notificationViewModel = notificationViewModel,
                authViewModel = authViewModel,
                actions = { showNotifications = !showNotifications }
            )
        },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = { AddMedicationButton(onClick = { navController.navigate(AppScreens.AddMedicationScreen.route) }) },
        containerColor = White,
    ) { padding ->
        Box {
            val medicines by medicineViewModel.filteredMedicines.collectAsState()
            val medication by medicineViewModel.medicines.collectAsState()
            val currentFilter by medicineViewModel.filter.collectAsState()

            LazyColumn(
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 30.dp)
                    .padding(bottom = 90.dp)
            ) {
                item { SuggestionsCard(navController) }

                item {
                    Text(
                        text = "Proxima medicación",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                if (medication.isEmpty()) {
                    item { EmptyMedicationState(filter = MedicineStatus.ALL) }
                } else {
                    item { FilterSection(viewModel = medicineViewModel) }

                    if(medicines.isEmpty()) {
                        item { EmptyMedicationState(filter = currentFilter) }
                    }

                    items(medicines) { medicine ->
                        MedicationCard(
                            medicine = medicine,
                            onTaken = { medicineViewModel.markAsTaken(medicine) },
                            onSkipped = { medicineViewModel.markAsSkipped(medicine) },
                            onInfo = { navController.navigate("detail/${it.id}") },
                            onEdit = { navController.navigate("edit/${it.id}") },
                            onDelete = { medicineToDelete = it }
                        )
                    }
                }
            }

            if (showNotifications) {
                NotificationsOverlay(
                    padding = padding.calculateTopPadding(),
                    viewModel = notificationViewModel,
                    onDismiss = { showNotifications = false },
                    onSeeAllClick = { navController.navigate(AppScreens.AllNotificationsScreen.route) },
                    onNotificationClick = { navController.navigate("notification_details_screen/${it.id}") }
                )
            }
        }
    }

    if (medicineToDelete != null) {
        AlertDialog(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.square_xmark_solid_full),
                        contentDescription = "Eliminar medicamento",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Text("Eliminar medicamento")
                }
            },
            text = { Text("¿Segura que deseas eliminarlo?") },
            containerColor = White,
            onDismissRequest = { medicineToDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    medicineViewModel.deleteMedicine(medicineToDelete!!)
                    medicineToDelete = null
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { medicineToDelete = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}