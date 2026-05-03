package com.tenko.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.data.serializable.UserUpdate
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.InfoRow
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ClinicalHistoryScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    // 1. Obtenemos el usuario del estado del ViewModel (es reactivo)
    val user = authViewModel.currentUser

    // 2. Si por alguna razón el usuario es nulo (ej. refresco manual),
    // lo pedimos una SOLA VEZ al entrar.
    LaunchedEffect(Unit) {
        if (user == null) {
            authViewModel.getUser(navController)
        }
    }

    var newValue by remember { mutableStateOf("") }
    var showInput by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Mi historial clínico",
                onBackClick = { navController.popBackStack() }
            )
        },
        contentColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(paddingValues)
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    scope.launch {
                        isRefreshing = true
                        delay(2000)
                        navController.popBackStack()
                        navController.navigate(AppScreens.UpdateProfileScreen.route)
                        isRefreshing = false
                    }
                },
            ) {
                LazyColumn(Modifier.fillMaxSize()) {
                    item {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize()
                        ) {
                            Text(
                                text = "Tus datos personales",
                                modifier = Modifier.padding(vertical = 16.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = Tekhelet
                            )

                            InfoRow(
                                label = "Nombre",
                                value = user?.name ?: "Jane Doe",
                                onClick = { showInput = true }
                            )

                            if(showInput) {
                                Column {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = newValue,
                                        onValueChange = { newValue = it },
                                        placeholder = { Text("Ingresa el nuevo nombre:") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = White,
                                            unfocusedContainerColor = AntiFlashWhite,
                                            focusedBorderColor = PompAndPower,
                                            unfocusedBorderColor = Color.Transparent,
                                            unfocusedPlaceholderColor = Color.Gray
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row {
                                        TextButton(onClick = { showInput = false }) {
                                            Text("Cancelar", color = PompAndPower)
                                        }
                                        TextButton(onClick = {
                                            authViewModel.updateUser(UserUpdate(name = newValue))
                                            scope.launch {
                                                isRefreshing = true
                                                delay(2000)
                                                navController.popBackStack()
                                                navController.navigate(AppScreens.UpdateProfileScreen.route)
                                                isRefreshing = false
                                            }
                                            showInput = false
                                        }) {
                                            Text("Cambiar nombre", color = Tekhelet)
                                        }
                                    }
                                }
                            }

                            InfoRow(
                                label = "Correo electrónico",
                                value = user?.email ?: "tenko@myst.com"
                            )
                        }
                    }
                }
            }
        }

    }

}