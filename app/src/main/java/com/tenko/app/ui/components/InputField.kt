package com.tenko.app.ui.components

import android.util.Patterns.EMAIL_ADDRESS
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.data.api.getAddressByCP
import com.tenko.app.data.api.loadCountries
import com.tenko.app.regex.PasswordRequirement
import com.tenko.app.regex.formatAsYouType
import com.tenko.app.regex.hasDigit
import com.tenko.app.regex.hasLowerCase
import com.tenko.app.regex.hasMinLength
import com.tenko.app.regex.hasNoSpaces
import com.tenko.app.regex.hasSpecialChar
import com.tenko.app.regex.hasUpperCase
import com.tenko.app.regex.isValidEmail
import com.tenko.app.regex.isValidNumber
import com.tenko.app.regex.isValidPassword
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale.getDefault

@Composable
fun nameInput(
    enableWhiteSpace: Boolean = true,
    label: String = "Nombres (sin apellidos)",
    initialValue: String = ""
): Pair<String, String> {
    var name by remember { mutableStateOf(initialValue) }
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
            if (newText.all { it.isLetter() || it.isWhitespace() }) {
                if (!enableWhiteSpace) {
                    if (newText.length <= 10)
                        if (!newText.contains(" "))
                            name = newText.split(" ")
                                .joinToString(" ") { word ->
                                    word.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase() else it.toString()
                                    }
                                }
                } else if (newText.length <= 20) {
                    // Solo letras y espacios
                    // Permitir solo letras y espacios
                    val cleanText = newText.filter {
                        it.isLetter() || it.isWhitespace()
                    }

                    // Separar palabras ignorando múltiples espacios
                    val words = cleanText
                        .split("\\s+".toRegex())
                        .filter { it.isNotBlank() }

                    // Limitar a máximo 2 nombres
                    if (words.size <= 2) {
                        val formatted = words.joinToString(" ") { word ->
                            word.lowercase()
                                .replaceFirstChar {
                                    it.uppercase()
                                }
                        }

                        // Mantener el espacio mientras escribe
                        name =
                            if (cleanText.endsWith(" ") && words.isNotEmpty() && words.size < 2) {
                                "$formatted "
                            } else {
                                formatted
                            }
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
            if (enableWhiteSpace)
                Icon(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(35.dp),
                    painter = painterResource(id = R.drawable.user_regular_full),
                    contentDescription = "User icon"
                )
        },
        /*supportingText = {
            Text("Máximo ${name.length} de ${if(enableWhiteSpace) 20 else 10} caracteres", fontSize = 12.sp, color = SweetGrey)
        },*/
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
    Spacer(Modifier.height(6.dp))

    return name to initials
}

@Composable
fun blockedInput(
    label: String = "Campo bloqueado",
    initialValue: String = "",
    modifier: Modifier = Modifier
): String {
    var text by remember { mutableStateOf(initialValue) }

    OutlinedTextField(
        value = text,
        onValueChange = { },
        placeholder = { Text(text = label, fontSize = 14.sp) },
        supportingText = { Text(label, fontSize = 12.sp) },
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(12.dp),
        enabled = false,
        /*colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = AntiFlashWhite,
            focusedBorderColor = PompAndPower,
            unfocusedBorderColor = Color.Transparent,
            focusedTrailingIconColor = PompAndPower,
            unfocusedTrailingIconColor = SweetGrey,
            unfocusedPlaceholderColor = Color.Gray,
        ),*/
        modifier = modifier
    )
    Spacer(Modifier.height(6.dp))

    return text
}

@Composable
fun inputField(
    type: ContentType,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    error: String?,
    focusRequester: FocusRequester,
    imeAction: ImeAction,
    scrollState: ScrollState? = null,
    scope: CoroutineScope,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
): Pair<String, String> {
    val context = LocalContext.current
    val countries = loadCountries(context)
    var formatted by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }
    var selected by remember {
        mutableStateOf(countries.firstOrNull { it.iso == "MX" } ?: countries.first())
    }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column {
        Text(
            text = "Teléfono del consultorio",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CountryDropdown(
                countries = countries,
                selected = selected,
                onSelect = {
                    selected = it
                    // Si el país cambia, reseteamos el número para evitar confusiones
                    onValueChange(TextFieldValue(""))
                    phoneNumber = ""
                }
            )

            OutlinedTextField(
                value = value,
                onValueChange = { input ->
                    if (input.text.any { !it.isDigit() && !it.isWhitespace() }) {
                        Toast.makeText(
                            context,
                            "Solo se permiten números",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OutlinedTextField
                    }
                    // Solo números
                    val digits = input.text.filter { it.isDigit() }
                    if (digits.length <= 10) {
                        phoneNumber = digits

                        // Formatear con el código del país
                        formatted = formatAsYouType(
                            raw = digits,
                            regionCode = selected.iso
                        )
                        isValid = isValidNumber(
                            number = digits,
                            regionCode = selected.iso
                        )
                        onValueChange(
                            TextFieldValue(
                                text = formatted,
                                selection = TextRange(formatted.length)
                            ) // Mantener el cursor al final
                        )
                    }
                },
                modifier = Modifier
                    .defaultMinSize(minHeight = 66.dp)
                    .focusRequester(focusRequester)
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            scope.launch {
                                delay(250)
                                bringIntoViewRequester.bringIntoView()
                                scrollState?.animateScrollBy(150f)
                            }
                        }
                    }
                    .semantics {
                        contentType = ContentType.PhoneNumber
                    },
                placeholder = { Text("Ej: 33 3225 8014", fontSize = 14.sp) },
                trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 4.dp),
                        painter = painterResource(id = R.drawable.phone_solid_full),
                        contentDescription = "Icono de teléfono"
                    )
                },
                isError = !isValid || error != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onNext?.invoke() },
                    onDone = { onDone?.invoke() }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = AntiFlashWhite,
                    focusedBorderColor = PompAndPower,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTrailingIconColor = PompAndPower,
                    unfocusedTrailingIconColor = SweetGrey,
                    unfocusedPlaceholderColor = Color.Gray,
                )
            )
        }

        AnimatedVisibility(
            visible = phoneNumber.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "Número ${if (isValid) "completo" else "incompleto"}: ${selected.code} $formatted",
                modifier = Modifier.padding(start = 6.dp),
                color = if (isValid) Color.LightGray else MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(6.dp))
    }

    return phoneNumber to formatted
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    error: String?,
    focusRequester: FocusRequester,
    imeAction: ImeAction,
    scrollState: ScrollState? = null,
    scope: CoroutineScope,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column {
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 12.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = value,
                onValueChange = { newText ->
                    if (newText.all { it.isDigit() }) {
                        if (newText.length > 5) {
                            Toast.makeText(
                                context,
                                "El código postal no puede tener más de 5 dígitos",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@OutlinedTextField
                        }
                        onValueChange(newText)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 66.dp)
                    .semantics {
                        contentType = ContentType.PostalCode
                    }
                    .focusRequester(focusRequester)
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent { state ->
                        if (state.isFocused) {
                            scope.launch {
                                delay(250)
                                bringIntoViewRequester.bringIntoView()
                                scrollState?.animateScrollBy(150f)
                            }
                        }
                    },
                placeholder = { Text(text = placeholder, fontSize = 14.sp) },
                isError = error != null || (if (value.isEmpty()) false else value.length != 5),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onNext?.invoke() },
                    onDone = { onDone?.invoke() }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = AntiFlashWhite,
                    focusedBorderColor = PompAndPower,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTrailingIconColor = PompAndPower,
                    unfocusedTrailingIconColor = SweetGrey,
                    unfocusedPlaceholderColor = Color.Gray,
                ),
            )

            IconButton(
                onClick = { onNext?.invoke() },
                modifier = Modifier.size(66.dp),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = PompAndPower,
                    contentColor = White
                ),
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.magnifying_glass_solid_full),
                        contentDescription = "Search icon",
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
        }

        AnimatedVisibility(
            visible = value.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val text =
                if (value.length != 5) "El código postal debe tener 5 dígitos" else "Código postal válido"
            Text(
                text = text,
                modifier = Modifier.padding(start = 6.dp),
                color = if (value.length == 5) Color.LightGray else MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(6.dp))
    }
}

@Composable
fun FormTextField(
    type: ContentType,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    error: String?,
    focusRequester: FocusRequester,
    imeAction: ImeAction,
    scrollState: ScrollState? = null,
    scope: CoroutineScope,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val colors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = AntiFlashWhite,
        focusedBorderColor = PompAndPower,
        unfocusedBorderColor = Color.Transparent,
        focusedTrailingIconColor = PompAndPower,
        unfocusedTrailingIconColor = SweetGrey,
        unfocusedPlaceholderColor = Color.Gray,
    )
    val context = LocalContext.current

    Column {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        when (type) {
            ContentType.PersonFirstName, ContentType.PersonLastName -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newText ->
                        if (newText.all { it.isLetter() }) {
                            if (newText.length > 10)
                                Toast.makeText(context, "Máximo 10 caracteres", Toast.LENGTH_SHORT)
                                    .show()
                            else {
                                // Solo letras y espacios
                                val cleanText = newText.filter { it.isLetter() }

                                // Separar palabras y limitar a 2 nombres
                                val words = cleanText
                                    .trim()
                                    .split("\\s+".toRegex())
                                    .filter { it.isNotBlank() }

                                // Formatear cada palabra
                                val formatted = words.joinToString(" ") { word ->
                                    word.lowercase()
                                        .replaceFirstChar { char ->
                                            char.uppercase()
                                        }
                                }

                                onValueChange(formatted)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 66.dp)
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { state ->
                            if (state.isFocused) {
                                scope.launch {
                                    delay(250)
                                    bringIntoViewRequester.bringIntoView()
                                    scrollState?.animateScrollBy(150f)
                                }
                            }
                        },
                    placeholder = { Text(placeholder, fontSize = 14.sp) },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onNext?.invoke() },
                        onDone = { onDone?.invoke() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = colors,
                )
            }

            ContentType.NewUsername -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newText ->
                        if (newText.all { it.isLetter() || it.isWhitespace() }) {
                            if (newText.length <= 20) {
                                // Solo letras y espacios
                                // Permitir solo letras y espacios
                                val cleanText = newText.filter {
                                    it.isLetter() || it.isWhitespace()
                                }

                                // Separar palabras ignorando múltiples espacios
                                val words = cleanText
                                    .split("\\s+".toRegex())
                                    .filter { it.isNotBlank() }

                                // Limitar a máximo 2 nombres
                                if (words.size <= 2) {
                                    val formatted = words.joinToString(" ") { word ->
                                        word.lowercase()
                                            .replaceFirstChar {
                                                it.uppercase()
                                            }
                                    }

                                    // Mantener el espacio mientras escribe
                                    onValueChange(
                                        if (cleanText.endsWith(" ") && words.isNotEmpty() && words.size < 2) {
                                            "$formatted "
                                        } else {
                                            formatted
                                        }
                                    )
                                }
                            } else
                                Toast.makeText(context, "Máximo 20 caracteres", Toast.LENGTH_SHORT)
                                    .show()
                        } else
                            Toast.makeText(
                                context,
                                "Solo letras y espacios",
                                Toast.LENGTH_SHORT
                            ).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 66.dp)
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { state ->
                            if (state.isFocused) {
                                scope.launch {
                                    delay(250)
                                    bringIntoViewRequester.bringIntoView()
                                    scrollState?.animateScrollBy(150f)
                                }
                            }
                        }
                        .semantics {
                            contentType = ContentType.NewUsername
                        },
                    placeholder = { Text(text = placeholder, fontSize = 14.sp) },
                    trailingIcon = {
                        Icon(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(35.dp),
                            painter = painterResource(id = R.drawable.user_regular_full),
                            contentDescription = "User icon"
                        )
                    },
                    isError = error != null || value.length > 20,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onNext?.invoke() },
                        onDone = { onDone?.invoke() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = colors,
                )
            }

            ContentType.EmailAddress -> {
                val emailError = value.isNotEmpty() && !EMAIL_ADDRESS.matcher(value).matches()
                val autofill = LocalAutofill.current
                val autofillNode = AutofillNode(
                    autofillTypes = listOf(AutofillType.EmailAddress),
                    onFill = { onValueChange(it) }
                )
                LocalAutofillTree.current += autofillNode

                OutlinedTextField(
                    value = value,
                    onValueChange = { newText ->
                        onValueChange(
                            newText
                                .lowercase(getDefault())
                                .filter {
                                    it.isLetterOrDigit() || it in "@._-"
                                }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 66.dp)
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { state ->
                            if (state.isFocused) {
                                scope.launch {
                                    delay(250)
                                    bringIntoViewRequester.bringIntoView()
                                    scrollState?.animateScrollBy(150f)
                                }
                            }
                        }
                        .onGloballyPositioned {
                            autofillNode.boundingBox = it.boundsInWindow()
                        }
                        .onFocusChanged { focusState ->
                            autofill?.let {
                                if (focusState.isFocused)
                                    it.requestAutofillForNode(autofillNode)
                                else
                                    it.cancelAutofillForNode(autofillNode)
                            }
                        }
                        .semantics {
                            contentType = ContentType.EmailAddress
                        },
                    placeholder = { Text(placeholder, fontSize = 14.sp) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.envelope_regular_full),
                            contentDescription = "Envelope icon",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(35.dp),
                        )
                    },
                    isError = error != null || emailError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Email,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onNext?.invoke() },
                        onDone = { onDone?.invoke() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = colors,
                )
            }

            ContentType.NewPassword -> {
                var passwordError by remember { mutableStateOf(false) }
                var passwordVisible by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = value,
                    onValueChange = { newText ->
                        if (!newText.contains(" ")) {
                            onValueChange(newText)
                            passwordError = !isValidPassword(newText)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 66.dp)
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { state ->
                            if (state.isFocused) {
                                scope.launch {
                                    delay(250)
                                    bringIntoViewRequester.bringIntoView()
                                    scrollState?.animateScrollBy(150f)
                                }
                            }
                        }
                        .semantics {
                            contentType = ContentType.NewPassword
                        },
                    placeholder = { Text(placeholder, fontSize = 14.sp) },
                    trailingIcon = {
                        val image =
                            if (passwordVisible) R.drawable.eye_regular_full else R.drawable.eye_slash_regular_full
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(id = image),
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(35.dp),
                            )
                        }
                    },
                    isError = error != null || passwordError,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onNext?.invoke() },
                        onDone = { onDone?.invoke() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = colors
                )
            }

            ContentType.Password -> {
                var passwordError by remember { mutableStateOf(false) }
                var passwordVisible by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = value,
                    onValueChange = { newText ->
                        if (!newText.contains(" ")) {
                            onValueChange(newText)
                            passwordError = !isValidPassword(newText)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 66.dp)
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { state ->
                            if (state.isFocused) {
                                scope.launch {
                                    delay(250)
                                    bringIntoViewRequester.bringIntoView()
                                    scrollState?.animateScrollBy(150f)
                                }
                            }
                        }
                        .semantics {
                            contentType = ContentType.Password
                        },
                    placeholder = { Text("Contraseña", fontSize = 14.sp) },
                    trailingIcon = {
                        val image =
                            if (passwordVisible) R.drawable.eye_regular_full else R.drawable.eye_slash_regular_full
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(id = image),
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(35.dp),
                            )
                        }
                    },
                    isError = error != null || passwordError,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onNext?.invoke() },
                        onDone = { onDone?.invoke() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = colors
                )
            }

            ContentType.AddressLocality -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newText ->
                        if (newText.all { it.isLetterOrDigit() || it.isWhitespace() }) {
                            if (newText.length > 25)
                                Toast.makeText(context, "Máximo 25 caracteres", Toast.LENGTH_SHORT)
                                    .show()
                            else {
                                onValueChange(
                                    newText.split(" ")
                                        .joinToString(" ") { word ->
                                            word.replaceFirstChar {
                                                if (it.isLowerCase()) it.titlecase() else it.toString()
                                            }
                                        }
                                )
                            }
                        } else
                            Toast.makeText(
                                context,
                                "Solo letras, números y espacios",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 66.dp)
                        .semantics {
                            contentType = ContentType.AddressLocality
                        }
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { state ->
                            if (state.isFocused) {
                                scope.launch {
                                    delay(250)
                                    bringIntoViewRequester.bringIntoView()
                                    scrollState?.animateScrollBy(150f)
                                }
                            }
                        },
                    placeholder = { Text(placeholder, fontSize = 14.sp) },
                    trailingIcon = {
                        Icon(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(35.dp),
                            painter = painterResource(id = R.drawable.hospital_regular_full),
                            contentDescription = "Building icon"
                        )
                    },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onNext?.invoke() },
                        onDone = { onDone?.invoke() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = colors,
                )
            }

            ContentType.AddressStreet -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newText ->
                        if (newText.all { it.isLetterOrDigit() || it.isWhitespace() }) {
                            if (newText.length > 50)
                                Toast.makeText(context, "Máximo 50 caracteres", Toast.LENGTH_SHORT)
                                    .show()
                            else {
                                onValueChange(
                                    newText.split(" ")
                                        .joinToString(" ") { word ->
                                            word.replaceFirstChar {
                                                if (it.isLowerCase()) it.titlecase() else it.toString()
                                            }
                                        }
                                )
                            }
                        } else
                            Toast.makeText(
                                context,
                                "Solo letras, números y espacios",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 66.dp)
                        .semantics {
                            contentType = ContentType.AddressStreet
                        }
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { state ->
                            if (state.isFocused) {
                                scope.launch {
                                    delay(250)
                                    bringIntoViewRequester.bringIntoView()
                                    scrollState?.animateScrollBy(150f)
                                }
                            }
                        },
                    placeholder = { Text(placeholder, fontSize = 14.sp) },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onNext?.invoke() },
                        onDone = { onDone?.invoke() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = colors,
                )
            }

            ContentType.PersonFullName -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newText ->
                        if (newText.all { it.isLetterOrDigit() || it.isWhitespace() }) {
                            if (newText.length > 25)
                                Toast.makeText(context, "Máximo 25 caracteres", Toast.LENGTH_SHORT)
                                    .show()
                            else
                                onValueChange(newText)
                        } else
                            Toast.makeText(
                                context,
                                "Solo letras, números y espacios",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 66.dp)
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { state ->
                            if (state.isFocused) {
                                scope.launch {
                                    delay(250)
                                    bringIntoViewRequester.bringIntoView()
                                    scrollState?.animateScrollBy(150f)
                                }
                            }
                        },
                    placeholder = { Text(placeholder, fontSize = 14.sp) },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onNext?.invoke() },
                        onDone = { onDone?.invoke() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = colors,
                )
            }

            ContentType.AddressAuxiliaryDetails -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newText ->
                        if (newText.all { it.isLetterOrDigit() || it.isWhitespace() }) {
                            if (newText.length > 50)
                                Toast.makeText(context, "Máximo 50 caracteres", Toast.LENGTH_SHORT)
                                    .show()
                            else
                                onValueChange(newText)
                        } else
                            Toast.makeText(
                                context,
                                "Solo letras, números y espacios",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 66.dp)
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { state ->
                            if (state.isFocused) {
                                scope.launch {
                                    delay(250)
                                    bringIntoViewRequester.bringIntoView()
                                    scrollState?.animateScrollBy(150f)
                                }
                            }
                        },
                    placeholder = { Text(placeholder, fontSize = 14.sp) },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { onNext?.invoke() },
                        onDone = { onDone?.invoke() }
                    ),
                    maxLines = 5,
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = colors,
                )
            }

            else -> {}
        }

        if (type == ContentType.EmailAddress) {
            AnimatedVisibility(
                visible = isValidEmail(value),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "Correo electrónico válido",
                    modifier = Modifier.padding(start = 6.dp),
                    color = PompAndPower,
                    fontSize = 12.sp
                )
            }
        }
        if (type == ContentType.Password || type == ContentType.NewPassword) {
            AnimatedVisibility(
                visible = isValidPassword(value),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "Contraseña segura",
                    modifier = Modifier.padding(start = 6.dp),
                    color = PompAndPower,
                    fontSize = 12.sp
                )
            }
        }
        if (type == ContentType.NewPassword) {
            val minLengthValid = hasMinLength(value)
            val upperCaseValid = hasUpperCase(value)
            val lowerCaseValid = hasLowerCase(value)
            val digitValid = hasDigit(value)
            val specialCharValid = hasSpecialChar(value)
            val noSpacesValid = hasNoSpaces(value)

            AnimatedVisibility(
                visible = (!minLengthValid || !upperCaseValid || !lowerCaseValid || !digitValid || !specialCharValid || !noSpacesValid) && value.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PasswordRequirement(
                        text = "Al menos 8 caracteres, máximo 16",
                        isValid = minLengthValid
                    )
                    PasswordRequirement(text = "Una mayúscula", isValid = upperCaseValid)
                    PasswordRequirement(text = "Una minúscula", isValid = lowerCaseValid)
                    PasswordRequirement(text = "Un número", isValid = digitValid)
                    PasswordRequirement(
                        text = "Un caracter especial (_-¡!@#\$%^&*(),.¿?\":;/{}|<>)",
                        isValid = specialCharValid
                    )
                    PasswordRequirement(text = "Sin espacios", isValid = noSpacesValid)
                }
            }
        }

        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = error ?: "",
                modifier = Modifier.padding(start = 6.dp),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(6.dp))
    }
}