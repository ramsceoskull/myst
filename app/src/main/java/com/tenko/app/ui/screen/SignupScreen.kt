package com.tenko.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.data.serializable.UserCreate
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.regex.isValidEmail
import com.tenko.app.regex.isValidPassword
import com.tenko.app.ui.components.FormTextField
import com.tenko.app.ui.components.LoginRedirectText
import com.tenko.app.ui.components.TermsAndPrivacyText
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val nameFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val initials by remember(name) {
        derivedStateOf {
            name
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.first().uppercaseChar() }
                .joinToString("")
        }
    }

    var isLoading by remember { mutableStateOf(false) }
    val isFormValid by remember(name, email, password) {
        mutableStateOf(name.isNotEmpty() && isValidEmail(email) && isValidPassword(password))
    }

    fun onDone() {
        if (name.isEmpty() || !isValidEmail(email) || !isValidPassword(password)) {
            nameError = if (name.isBlank()) "El nombre no puede estar vacío" else null
            emailError =
                if (email.isBlank()) "El correo no puede estar vacío" else if (!isValidEmail(email)) "Correo incompleto" else null
            if (!isValidPassword(password)) passwordError =
                "La contraseña debe tener al menos 8 caracteres.\nIncluyendo mayúsculas, minúsculas, números y caracteres especiales."
            return
        }
        isLoading = true
        keyboardController?.hide()

        val newUser = UserCreate(
            name = name,
            email = email,
            password = password,
            initials = if (initials.length == 2) initials else name.take(2).uppercase(),
            picture = null
        )
        viewModel.createUser(newUser, navController)
        scope.launch {
            delay(2000) // Simula un proceso de registro
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(White)
            .verticalScroll(scrollState)
            .padding(start = 25.dp, top = 60.dp, end = 25.dp, bottom = 15.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenida a Myst",
            color = Tekhelet,
            fontSize = 40.sp,
            fontFamily = StarsLove,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 45.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Regístrate",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Crea tu cuenta para empezar a usar Myst",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormTextField(
            type = ContentType.NewUsername,
            value = name,
            onValueChange = {
                name = it
                nameError = null
            },
            label = "",
            placeholder = "Nombre (sin apellidos)",
            error = nameError,
            focusRequester = nameFocus,
            imeAction = ImeAction.Next,
            onNext = {
                if (name.isBlank()) {
                    nameError = "El nombre no puede estar vacío"
                    return@FormTextField
                }
                emailFocus.requestFocus()
            },
            scrollState = scrollState,
            scope = scope
        )

        Spacer(modifier = Modifier.height(6.dp))

        FormTextField(
            type = ContentType.EmailAddress,
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = "",
            placeholder = "correo@ejemplo.com",
            error = emailError,
            focusRequester = emailFocus,
            imeAction = ImeAction.Next,
            onNext = {
                if (!isValidEmail(email)) {
                    emailError = "Correo incompleto"
                    return@FormTextField
                }
                passwordFocus.requestFocus()
            },
            scrollState = scrollState,
            scope = scope
        )

        Spacer(modifier = Modifier.height(6.dp))

        FormTextField(
            type = ContentType.NewPassword,
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = "",
            placeholder = "Nueva contraseña",
            error = passwordError,
            focusRequester = passwordFocus,
            imeAction = ImeAction.Done,
            onDone = { onDone() },
            scrollState = scrollState,
            scope = scope
        )

        TextButton(
            onClick = { onDone() },
            modifier = Modifier
                .padding(vertical = 24.dp)
                .fillMaxWidth()
                .height(66.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Tekhelet,
                contentColor = White,
                disabledContainerColor = SweetGrey,
                disabledContentColor = White
            ),
            content = {
                Text(
                    text = if (isLoading) "Procesando..." else "Continuar",
                    fontSize = 25.sp,
                    fontFamily = StarsLove,
                    fontWeight = FontWeight.ExtraLight,
                    modifier = Modifier.offset(y = 4.dp)
                )
            }
        )

        LoginRedirectText(navController)

        Spacer(modifier = Modifier.weight(1f))

        TermsAndPrivacyText(navController)
    }
}
