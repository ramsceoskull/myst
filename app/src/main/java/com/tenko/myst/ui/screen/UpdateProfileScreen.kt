package com.tenko.myst.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tenko.myst.data.api.TokenManager
import com.tenko.myst.data.view.AuthViewModel
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.DeleteAccountRow
import com.tenko.myst.ui.components.InfoRow
import com.tenko.myst.ui.components.PhotoActionsRow
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

@Composable
fun UpdateProfileScreen(navController: NavController, viewModel: AuthViewModel, tokenManager: TokenManager) {
    LaunchedEffect(Unit) {
        viewModel.getUser()
    }

    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val user = viewModel.currentUser

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Editar Perfil",
                onBackClick = { navController.popBackStack() }
            )
        },
        contentColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(White)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                PhotoActionsRow(
                    imageUrl = "https://picsum.photos/200",
                    onEditClick = { /* Lógica para editar foto */ },
                    onRemoveClick = { /* Lógica para eliminar foto */ }
                )

                Text(
                    text = "Tus datos personales",
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Tekhelet
                )

                InfoRow(
                    label = "Nombre",
                    value = user?.name ?: "Jane Doe",
                    onClick = { /* Lógica para editar nombre */ }
                )

                InfoRow(
                    label = "Correo electrónico",
                    value = user?.email ?: "tenko@myst.com",
                    showArrow = false,
                    onClick = { /* Lógica para editar correo */ }
                )

                Text(
                    text = "Otro",
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Tekhelet
                )

                DeleteAccountRow(
                    label = "Eliminar cuenta",
                    onClick = { showDialog = true }
                )

                /*if(showDialog)
                    Column {
                        Text("Esta acción no se puede deshacer.\nPor favor, ingresa tu contraseña para confirmar:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Contraseña") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                        Row {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancelar")
                            }
                            TextButton(onClick = {
                                viewModel.deleteUser(password, tokenManager, navController)
//                                showDialog = false
                            }) {
                                Text("Eliminar definitivamente")
                            }
                        }
                    }*/
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("¿Estás segura?") },
                    text = {
                        Column {
                            Text("Esta acción no se puede deshacer.\nPor favor, ingresa tu contraseña para confirmar:")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Contraseña") },
                                visualTransformation = PasswordVisualTransformation()
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if(password.isNotBlank()) {
                                viewModel.deleteUser(password, tokenManager, navController)
                                showDialog = false
                            }
                        }) {
                            Text("Eliminar definitivamente")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}