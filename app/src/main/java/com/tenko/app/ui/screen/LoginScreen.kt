package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.tenko.app.ui.components.SignupRedirectText
import com.tenko.app.ui.components.emailInput
import com.tenko.app.ui.components.passwordInput
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp).padding(horizontal = 25.dp)
        ) {
            Text(
                text = "Bienvenida de nuevo\na Myst",
                color = Tekhelet,
                fontSize = 34.sp,
                fontFamily = StarsLove,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 45.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Inicio de sesión",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            val email = emailInput(false)
            Spacer(modifier = Modifier.height(6.dp))
            val password = passwordInput(false)
            val isFormValid = isValidEmail(email) && isValidPassword(password)

            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    if(isValidEmail(email))
                        viewModel.forgotPassword(email, navController)
                    else
                        Toast.makeText(context, "Por favor ingresa tu correo electrónico para recuperar tu contraseña", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = PompAndPower,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(email.trim(), password.trim(), navController, tokenManager) },
                enabled = isFormValid,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Tekhelet,
                    disabledContainerColor = SweetGrey
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    color = White,
                    fontSize = 25.sp,
                    fontFamily = StarsLove,
                    fontWeight = FontWeight.ExtraLight,
                    modifier = Modifier.offset(y = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AutoScrollingCarousel()

        Spacer(modifier = Modifier.height(24.dp))

        SignupRedirectText(navController)
    }
}