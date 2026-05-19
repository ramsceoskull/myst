package com.tenko.app.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.api.TokenManager
import com.tenko.app.data.model.DialogButton
import com.tenko.app.data.model.DialogTitle
import com.tenko.app.data.serializable.UserUpdate
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.regex.isValidPassword
import com.tenko.app.ui.components.AlertDialog
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.DeleteAccountRow
import com.tenko.app.ui.components.FormTextField
import com.tenko.app.ui.components.InfoRow
import com.tenko.app.ui.components.PhotoActionsSection
import com.tenko.app.ui.components.SectionTitle
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    tokenManager: TokenManager
) {
    val user = authViewModel.currentUser
    val isRefreshing = authViewModel.isLoading
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        if (user == null)
            authViewModel.getUser(navController)
    }

    var showDialog by remember { mutableStateOf(false) }
    var showInput by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    val initials by remember(newName) {
        derivedStateOf {
            newName
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.first().uppercaseChar() }
                .joinToString("")
        }
    }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

//    Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            authViewModel.updateProfilePicture(it, context)
        }
    }

    fun onDone() {
        if (newName.isBlank()) {
            nameError = "El nombre no puede estar vacío"
            return
        }

        keyboardController?.hide()
        authViewModel.updateUser(
            updateData = UserUpdate(
                name = newName,
                initials = if (initials.length == 2) initials else newName.take(2).uppercase()
            ),
            context = context
        ) {
            Toast
                .makeText(
                    context,
                    "Nombre actualizado",
                    Toast.LENGTH_SHORT
                )
                .show()
        }
        showInput = false
    }

    fun onDeleteAccount() {
        if (!isValidPassword(password)) {
            passwordError =
                if (password.isBlank()) "La contraseña no puede estar vacía" else
                    if (!isValidPassword(password)) "La contraseña no cumple con los requisitos" else null
            return
        }

        authViewModel.deleteUser(
            password,
            tokenManager,
            navController
        )
        showDialog = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Editar Perfil",
                    onBackClick = { navController.popBackStack() }
                ) {}
            },
            contentColor = BackgroundColor
        ) { paddingValues ->
            val nameFocus = remember { FocusRequester() }
            val passwordFocus = remember { FocusRequester() }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { authViewModel.getUser(navController) },
                modifier = Modifier.padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 30.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    PhotoActionsSection(
                        imageUrl = user?.picture?.toUri(),
                        onEditClick = { galleryLauncher.launch("image/*") },
                        onRemoveClick = {
                            if (user?.picture.isNullOrEmpty()) {
                                Toast
                                    .makeText(
                                        context,
                                        "No tienes una foto de perfil que eliminar",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                                return@PhotoActionsSection
                            }
                            authViewModel.updateUser(UserUpdate(picture = ""), context) {
                                Toast
                                    .makeText(
                                        context,
                                        "Foto de perfil eliminada",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                    )

                    SectionTitle(text = "Tus datos personales")

                    InfoRow(
                        label = "Nombre",
                        value = user?.name ?: "Jane Doe",
                        showInput = showInput,
                        onClick = { showInput = true },
                        onCancel = { showInput = false },
                        onDone = { onDone() },
                        input = {
                            FormTextField(
                                type = ContentType.NewUsername,
                                value = newName,
                                onValueChange = {
                                    newName = it
                                    nameError = null
                                },
                                label = "",
                                placeholder = "Nombre (sin apellidos)",
                                error = nameError,
                                focusRequester = nameFocus,
                                imeAction = ImeAction.Next,
                                onNext = { onDone() },
                                scrollState = scrollState,
                                scope = scope
                            )
                        }
                    )

                    InfoRow(
                        label = "Iniciales",
                        value = user?.initials ?: "JD",
                    )

                    InfoRow(
                        label = "Correo electrónico",
                        value = user?.email ?: "tenko@myst.com",
                    )

                    SectionTitle(text = "Otro")

                    DeleteAccountRow(
                        label = "Eliminar cuenta",
                        onClick = { showDialog = true }
                    )
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = DialogTitle(
                            icon = R.drawable.trash_solid_full,
                            text = "¿Eliminar cuenta?"
                        ),
                        confirmButton = DialogButton("Eliminar") { onDeleteAccount() },
                        content = {
                            Text(
                                "Esta acción no se puede deshacer.\n" +
                                        "Por favor, ingresa tu contraseña para confirmar:"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            FormTextField(
                                type = ContentType.Password,
                                value = password,
                                onValueChange = {
                                    password = it
                                    passwordError = null
                                },
                                label = "",
                                placeholder = "Contraseña",
                                error = passwordError,
                                focusRequester = passwordFocus,
                                imeAction = ImeAction.Done,
                                onDone = { onDeleteAccount() },
                                scrollState = scrollState,
                                scope = scope
                            )
                        }
                    )
                }
            }
        }

        if (isRefreshing) {
            SplashScreen()
        }
    }
}