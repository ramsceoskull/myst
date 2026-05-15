package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.data.api.TokenManager
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.regex.isValidEmail
import com.tenko.app.regex.isValidPassword
import com.tenko.app.ui.components.AutoScrollingCarousel
import com.tenko.app.ui.components.FormTextField
import com.tenko.app.ui.components.SignupRedirectText
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isFormValid by remember(email, password) {
        mutableStateOf(isValidEmail(email) && isValidPassword(password))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(White)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
                .padding(horizontal = 25.dp)
        ) {
            Text(
                text = "Bienvenida de nuevo a Myst",
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

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Inicio de sesión",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

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
                onDone = {
                    if (!isValidPassword(password)) {
                        passwordError =
                            "La contraseña debe tener al menos 8 caracteres.\nIncluyendo mayúsculas, minúsculas, números y caracteres especiales."
                        return@FormTextField
                    }
                    // Aquí podrías iniciar sesión automáticamente si el formulario es válido
                    if (isValidEmail(email) && isValidPassword(password)) {
                        keyboardController?.hide()
                        viewModel.login(
                            email.trim(),
                            password.trim(),
                            navController,
                            tokenManager
                        )
                    }
                },
                scrollState = scrollState,
                scope = scope
            )

            TextButton(
                onClick = {
                    if (isValidEmail(email))
                        viewModel.forgotPassword(email, navController)
                    else
                        Toast.makeText(
                            context,
                            "Por favor ingresa tu correo electrónico para recuperar tu contraseña",
                            Toast.LENGTH_SHORT
                        ).show()
                },
                modifier = Modifier.align(Alignment.End),
                content = {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = PompAndPower,
                        fontSize = 13.sp
                    )
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(
                onClick = {
                    if (!isFormValid) {
                        Toast.makeText(
                            context,
                            "Por favor completa el formulario correctamente antes de iniciar sesión",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TextButton
                    }
                    viewModel.login(
                        email.trim(),
                        password.trim(),
                        navController,
                        tokenManager
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 66.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Tekhelet,
                    contentColor = White,
                ),
                content = {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 25.sp,
                        fontFamily = StarsLove,
                        fontWeight = FontWeight.ExtraLight,
                        modifier = Modifier.offset(y = 4.dp)
                    )
                }
            )
        }

        AutoScrollingCarousel()

        SignupRedirectText(navController)
    }
}