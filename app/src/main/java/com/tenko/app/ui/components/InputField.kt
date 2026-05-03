package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.regex.PasswordRequirement
import com.tenko.app.regex.hasDigit
import com.tenko.app.regex.hasLowerCase
import com.tenko.app.regex.hasMinLength
import com.tenko.app.regex.hasNoSpaces
import com.tenko.app.regex.hasSpecialChar
import com.tenko.app.regex.hasUpperCase
import com.tenko.app.regex.isValidEmail
import com.tenko.app.regex.isValidPassword
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.SweetGrey

@Composable
fun nameInput(enableWhiteSpace: Boolean = true, label: String = "Nombres (sin apellidos)"): Pair<String, String> {
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

    OutlinedTextField(
        value = name,
        onValueChange = { newText ->
            if(!enableWhiteSpace) {
                if(!newText.contains(" "))
                    name = newText.split(" ")
                        .joinToString(" ") { word ->
                            word.replaceFirstChar {
                                if(it.isLowerCase()) it.titlecase() else it.toString()
                            }
                        }
            } else {
                name = newText.split(" ")
                    .joinToString(" ") { word ->
                        word.replaceFirstChar {
                            if(it.isLowerCase()) it.titlecase() else it.toString()
                        }
                    }
            }
        },
        placeholder = { Text(text = label, fontSize = 14.sp) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text
        ),
        trailingIcon = {
            if(enableWhiteSpace)
                Icon(
                    modifier = Modifier.size(35.dp).padding(end = 4.dp),
                    painter = painterResource(id = R.drawable.user_regular_full),
                    contentDescription = "Icono de sobre"
                )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = AntiFlashWhite,
            focusedBorderColor = PompAndPower,
            unfocusedBorderColor = Color.Transparent,
            focusedTrailingIconColor = PompAndPower,
            unfocusedTrailingIconColor = SweetGrey,
            unfocusedPlaceholderColor = Color.Gray,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
    )
    Spacer(Modifier.height(8.dp))

    return name to initials
}

@Composable
fun emailInput(showWarnings: Boolean = true): String {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(
        autofillTypes = listOf(AutofillType.EmailAddress),
        onFill = { email = it }
    )

    LocalAutofillTree.current += autofillNode

    OutlinedTextField(
        value = email,
        onValueChange = { newText ->
            if(!newText.contains(" ")) {
                email = newText
                emailError = !isValidEmail(newText)
            }
        },
        placeholder = { Text(text = "Correo electrónico", fontSize = 14.sp) },
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
            unfocusedPlaceholderColor = Color.Gray,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .onGloballyPositioned {
                autofillNode.boundingBox = it.boundsInWindow()
            }
            .onFocusChanged { focusState ->
                autofill?.let {
                    if(focusState.isFocused)
                        it.requestAutofillForNode(autofillNode)
                    else
                        it.cancelAutofillForNode(autofillNode)
                }
            }
            .semantics {
                contentType = ContentType.EmailAddress
            }
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

    return email.trim()
}

@Composable
fun passwordInput(showWarnings: Boolean = true): String {
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val minLengthValid = hasMinLength(password)
    val upperCaseValid = hasUpperCase(password)
    val lowerCaseValid = hasLowerCase(password)
    val digitValid = hasDigit(password)
    val specialCharValid = hasSpecialChar(password)
    val noSpacesValid = hasNoSpaces(password)

    OutlinedTextField(
        value = password,
        onValueChange = { newText ->
            if(!newText.contains(" ")) {
                password = newText
                passwordError = !isValidPassword(newText)
            }
        },
        placeholder = { Text("Contraseña", fontSize = 14.sp) },
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
            unfocusedPlaceholderColor = Color.Gray,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .semantics {
                contentType = ContentType.Password
            }
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
                PasswordRequirement(text = "Un caracter especial (_-¡!@#\$%^&*(),.¿?\":;/{}|<>)", isValid = specialCharValid)
                PasswordRequirement(text = "Sin espacios", isValid = noSpacesValid)
            }
        }
    }

    return password.trim()
}