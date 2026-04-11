package com.tenko.myst.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.myst.R
import com.tenko.myst.regex.PasswordRequirement
import com.tenko.myst.regex.hasDigit
import com.tenko.myst.regex.hasLowerCase
import com.tenko.myst.regex.hasMinLength
import com.tenko.myst.regex.hasNoSpaces
import com.tenko.myst.regex.hasSpecialChar
import com.tenko.myst.regex.hasUpperCase
import com.tenko.myst.regex.isValidEmail
import com.tenko.myst.regex.isValidPassword
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.SweetGrey

@Composable
fun emailInput(showWarnings: Boolean = true): String {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = email,
        onValueChange = {
            email = it
            emailError = !isValidEmail(it)
        },
        label = { Text(text = "Correo electrónico", fontSize = 14.sp) },
        isError = emailError,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            Icon(
                modifier = Modifier.size(35.dp).padding(end = 4.dp),
                painter = painterResource(id = R.drawable.envelope_regular_full),
                contentDescription = "Icono de sobre"
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = AntiFlashWhite,
            focusedBorderColor = PompAndPower,
            unfocusedBorderColor = Color.Transparent,
            focusedTrailingIconColor = PompAndPower,
            unfocusedTrailingIconColor = SweetGrey,
            unfocusedLabelColor = Color.Gray,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
    )

    if(showWarnings) {
        if(!emailError) {
            if(email.isNotBlank())
                Text(
                    text = "Correo valido",
                    color = PompAndPower,
                    fontSize = 12.sp
                )
        } else {
            Text(
                text = "Correo invalido",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }
    Spacer(Modifier.height(8.dp))

    return email
}

@Composable
fun passwordInput(showWarnings: Boolean = true): String {
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false)}

    val minLengthValid = hasMinLength(password)
    val upperCaseValid = hasUpperCase(password)
    val lowerCaseValid = hasLowerCase(password)
    val digitValid = hasDigit(password)
    val specialCharValid = hasSpecialChar(password)
    val noSpacesValid = hasNoSpaces(password)

    OutlinedTextField(
        value = password,
        onValueChange = {
            password = it
            passwordError = !isValidPassword(it)
        },
        label = { Text("Contraseña", fontSize = 14.sp) },
        isError = passwordError,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) R.drawable.eye_regular_full else R.drawable.eye_slash_regular_full
            IconButton(onClick = {passwordVisible = !passwordVisible}) {
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                    modifier = Modifier.size(35.dp).padding(end = 4.dp)
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = AntiFlashWhite,
            focusedBorderColor = PompAndPower,
            unfocusedBorderColor = Color.Transparent,
            focusedTrailingIconColor = PompAndPower,
            unfocusedTrailingIconColor = SweetGrey,
            unfocusedLabelColor = Color.Gray,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
    )

    if(showWarnings) {
        if (!passwordError) {
            if(password.isNotBlank())
                Text(
                    text = "Contraseña segura",
                    color = PompAndPower,
                    fontSize = 12.sp
                )
        } else {
            Column( modifier = Modifier.fillMaxWidth() ) {
                PasswordRequirement(text = "Al menos 8 caracteres, máximo 16", isValid = minLengthValid)
                PasswordRequirement(text = "Una mayúscula", isValid = upperCaseValid)
                PasswordRequirement(text = "Una minúscula", isValid = lowerCaseValid)
                PasswordRequirement(text = "Un número", isValid = digitValid)
                PasswordRequirement(text = "Un caracter especial", isValid = specialCharValid)
                PasswordRequirement(text = "Sin espacios", isValid = noSpacesValid)
            }
        }
    }

    return password
}