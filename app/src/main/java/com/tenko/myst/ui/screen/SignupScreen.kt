package com.tenko.myst.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tenko.myst.R
import com.tenko.myst.navigation.AppScreens
import com.tenko.myst.regex.isValidEmail
import com.tenko.myst.regex.isValidPassword
import com.tenko.myst.ui.components.AutoScrollingCarousel
import com.tenko.myst.ui.components.LoginRedirectText
import com.tenko.myst.ui.components.TermsAndPrivacyText
import com.tenko.myst.ui.components.emailInput
import com.tenko.myst.ui.components.passwordInput
import com.tenko.myst.ui.theme.StarsLove
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

@Composable
fun SignupScreen(navController: NavController) {
    var acceptedTerms by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }
    /*val accepted = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("termsAccepted")
//        ?.observeAsState()

    val isAccepted = accepted?.value == true*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column( modifier = Modifier.padding(top = 60.dp).padding(horizontal = 25.dp) ) {
            Text(
                text = "Bienvenida\na Myst",
                color = Tekhelet,
                fontSize = 34.sp,
                fontFamily = StarsLove,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 45.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Regístrate",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Crea tu cuenta para empezar a usar Myst",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val email = emailInput()
            val password = passwordInput()
            val isFormValid = isValidEmail(email) && isValidPassword(password)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate(AppScreens.ValidateEmailScreen.route) },
                enabled = isFormValid/* && acceptedTerms*/,
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
                    text = "Continuar",
                    color = Color.White,
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

        Column(
            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginRedirectText(navController)
            TermsAndPrivacyText(navController)
        }
    }

    if (showTerms) {
        /*TermsDialog(
            pdfResId = R.raw.terms, // coloca tu PDF en res/raw
            onDismiss = { showTerms = false },
            onAccept = {
                acceptedTerms = true
                showTerms = false
            }
        )*/
        PdfViewerScreen(
//            pdfResId = R.raw.terms, // coloca tu PDF en res/raw
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            onAccept = {
                acceptedTerms = true
                showTerms = false
            },
            onDismiss = { showTerms = false }
        )
    }
}