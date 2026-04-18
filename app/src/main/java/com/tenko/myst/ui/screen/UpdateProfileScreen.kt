package com.tenko.myst.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.myst.data.api.TokenManager
import com.tenko.myst.data.serializable.UserUpdate
import com.tenko.myst.data.view.AuthViewModel
import com.tenko.myst.data.view.ProfilePictureViewModel
import com.tenko.myst.launcher.rememberCameraLauncher
import com.tenko.myst.launcher.rememberGalleryLauncher
import com.tenko.myst.navigation.AppScreens
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.DeleteAccountRow
import com.tenko.myst.ui.components.InfoRow
import com.tenko.myst.ui.components.PhotoActionsSection
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(navController: NavController, tokenManager: TokenManager, viewModel: AuthViewModel = viewModel()) {
    viewModel.getUser(navController)
    val user = viewModel.currentUser

    var newValue by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
            viewModel.updateProfilePicture(it, context)
        }

    }
   /* val galleryLauncher = rememberGalleryLauncher {

        val updateUserPhoto = UserUpdate(picture = it.toString())
        viewModel.updateUser(updateUserPhoto)
    }*/
    val cameraLauncher = rememberCameraLauncher(tempUri) { success ->
        if(success) {
            val updateUserPhoto = UserUpdate(picture = tempUri.toString())
            viewModel.updateUser(updateUserPhoto)
        }
    }

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
                            PhotoActionsSection(
                                imageUrl = user?.picture?.toUri(),
                                onEditClick = { galleryLauncher.launch("image/*") },
                                onRemoveClick = { viewModel.updateUser(UserUpdate(picture = "")) }
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
                                            viewModel.updateUser(UserUpdate(name = newValue))
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
        }
    }
}