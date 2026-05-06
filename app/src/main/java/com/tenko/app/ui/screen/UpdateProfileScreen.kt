package com.tenko.app.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.api.TokenManager
import com.tenko.app.data.serializable.UserUpdate
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.regex.isValidPassword
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.DeleteAccountRow
import com.tenko.app.ui.components.InfoRow
import com.tenko.app.ui.components.PhotoActionsSection
import com.tenko.app.ui.components.passwordInput
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    tokenManager: TokenManager
) {
    val user = authViewModel.currentUser

    LaunchedEffect(Unit) {
        if (user == null) {
            authViewModel.getUser(navController)
        }
    }

    var newValue by remember { mutableStateOf("") }
    val initials by remember(newValue) {
        derivedStateOf {
            newValue
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.first().uppercaseChar() }
                .joinToString("")
        }
    }
    var showDialog by remember { mutableStateOf(false) }
    var showInput by remember { mutableStateOf(false) }

    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

//    URI temporal para camara
    val tempUri = remember {
        val file = File.createTempFile("temp_photo", ".jpg", context.cacheDir)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

//    Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri : Uri? ->
        uri?.let {
            authViewModel.updateProfilePicture(it)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if(success)
            authViewModel.updateUser(UserUpdate(picture = tempUri.toString()))
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Editar Perfil",
                onBackClick = { navController.popBackStack() }
            )
        },
        contentColor = BackgroundColor
    ) { paddingValues ->
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
            modifier = Modifier
                .background(White)
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp)
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                PhotoActionsSection(
                    imageUrl = user?.picture?.toUri(),
                    onEditClick = { galleryLauncher.launch("image/*") },
                    onRemoveClick = { authViewModel.updateUser(UserUpdate(picture = "")) }
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
                    onClick = { showInput = true }
                )

                if(showInput) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newValue,
                            onValueChange = { newText ->
                                newValue = newText.split(" ")
                                    .joinToString(" ") { word ->
                                    word.replaceFirstChar {
                                        if(it.isLowerCase()) it.titlecase() else it.toString()
                                    }
                                }
                            },
                            placeholder = { Text("Ingresa el nuevo nombre:") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                keyboardType = KeyboardType.Text
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = White,
                                unfocusedContainerColor = AntiFlashWhite,
                                focusedBorderColor = PompAndPower,
                                unfocusedBorderColor = Color.Transparent,
                                unfocusedPlaceholderColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            TextButton(onClick = { showInput = false }) {
                                Text("Cancelar", color = RaisinBlack)
                            }
                            TextButton(
                                onClick = {
                                    if(newValue.isNotBlank()) {
                                        authViewModel.updateUser(UserUpdate(
                                            name = newValue, initials =
                                                if(initials.length == 2) initials
                                                else newValue.take(2).uppercase())
                                        )
                                        scope.launch {
                                            isRefreshing = true
                                            delay(2000)
                                            isRefreshing = false
                                        }
                                        showInput = false
                                    } else
                                        Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = White,
                                    containerColor = Tekhelet
                                ),
                                content = { Text("Cambiar nombre") }
                            )
                        }
                    }
                }

                InfoRow(
                    label = "Iniciales",
                    value = user?.initials ?: "JD",
                )

                InfoRow(
                    label = "Correo electrónico",
                    value = user?.email ?: "tenko@myst.com",
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
            }

            if (showDialog) {
                var password by remember { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if(password.isNotBlank()) {
                                    if(isValidPassword(password)) {
                                        authViewModel.deleteUser(password, tokenManager, navController)
                                        showDialog = false
                                    } else
                                        Toast.makeText(context, "La contraseña no cumple con los requisitos", Toast.LENGTH_SHORT).show()
                                } else
                                    Toast.makeText(context, "Por favor, ingresa tu contraseña", Toast.LENGTH_SHORT).show()
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
                            onClick = { showDialog = false },
                            content = { Text("Cancelar", color = SweetGrey) }
                        )
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.trash_can_regular_full),
                                contentDescription = null,
                            )
                            Text("¿Estás segura?")
                        }
                    },
                    text = {
                        Column {
                            Text("Esta acción no se puede deshacer.\nPor favor, ingresa tu contraseña para confirmar:")
                            Spacer(modifier = Modifier.height(8.dp))
                            password = passwordInput(false)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    containerColor = White,
                    titleContentColor = Tekhelet,
                    textContentColor = SweetGrey
                )
            }
        }
    }
}