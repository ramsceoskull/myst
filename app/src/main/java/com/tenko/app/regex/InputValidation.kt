package com.tenko.app.regex

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.ui.theme.Tekhelet

fun isValidEmail(email: String): Boolean {
    val regex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    return regex.matches(email)
}

fun isValidPassword(password: String): Boolean {
    val regex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}\$")
    return regex.matches(password)
}

fun hasMinLength(password: String) = password.length in 8..16
fun hasUpperCase(password: String) = password.any { it.isUpperCase() }
fun hasLowerCase(password: String) = password.any { it.isLowerCase() }
fun hasDigit(password: String) = password.any { it.isDigit() }
fun hasSpecialChar(password: String) = password.any { !it.isLetterOrDigit() }
fun hasNoSpaces(password: String) = !password.contains(" ")

@Composable
fun PasswordRequirement(text: String, isValid: Boolean) {
    val animatedColor by animateColorAsState(
        targetValue = if (isValid) Tekhelet else Color.Red,
        label = "ColorAnimation",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 2.dp)
    ) {
        AnimatedContent(
            targetState = isValid,
            label = "iconAnimation"
        ) { target ->
            Icon(
                painter = painterResource(id = if (target) R.drawable.check_solid_full else R.drawable.xmark_solid_full),
                contentDescription = "${if (target) "Valid" else "Invalid"} requirement",
                tint = animatedColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = animatedColor,
            fontSize = 12.sp
        )
    }
}