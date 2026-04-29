package com.tenko.app.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.theme.Monserrat
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.SweetGrey

@Composable
fun SignupRedirectText(navController: NavController) {
    val annotatedText = buildAnnotatedString {
        append("¿Nueva en Myst? ")

        pushStringAnnotation(tag = "signup", annotation = "signup")
        withStyle(
            SpanStyle(
                color = PompAndPower,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) { append("Regístrate") }
        pop()
    }

    ClickableText(
        text = annotatedText,
        style = TextStyle(
            color = Color.Gray,
            fontSize = 14.sp,
            fontFamily = Monserrat,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        ),
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                if (it.tag == "signup") {
                    navController.navigate(AppScreens.SignupScreen.route)
                }
            }
        }
    )
}

@Composable
fun LoginRedirectText(navController: NavController) {
    val annotatedText = buildAnnotatedString {
        append("¿Ya tienes una cuenta? ")

        pushStringAnnotation(tag = "login", annotation = "login")
        withStyle(
            SpanStyle(
                color = PompAndPower,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) { append("Inicia Sesión") }
        pop()
    }

    ClickableText(
        text = annotatedText,
        style = TextStyle(
            color = Color.Gray,
            fontSize = 14.sp,
            fontFamily = Monserrat,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        ),
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                if (it.tag == "login") {
                    navController.navigate(AppScreens.LoginScreen.route)
                }
            }
        }
    )
}

@Composable
fun TermsAndPrivacyText(navController: NavController) {
    val annotatedText = buildAnnotatedString {
        append("Al hacer clic en \"Continuar\", estás aceptando los\n")

        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(
            style = SpanStyle(
                color = PompAndPower,
                textDecoration = TextDecoration.Underline
            )
        ) { append("Términos de uso") }
        pop()

        append(" y ")

        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(
            style = SpanStyle(
                color = PompAndPower,
                textDecoration = TextDecoration.Underline
            )
        ) { append("Política de privacidad") }
        pop()
    }

    ClickableText(
        text = annotatedText,
        style = TextStyle(
            color = SweetGrey,
            fontSize = 12.sp,
            fontFamily = Monserrat,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        ),
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                when (it.tag) {
                    "TERMS" -> {
                        navController.navigate("terms")
                    }

                    "PRIVACY" -> {
                        navController.navigate("privacy")
                    }
                }
            }
        }
    )
}