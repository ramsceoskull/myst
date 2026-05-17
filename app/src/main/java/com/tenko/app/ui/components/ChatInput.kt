package com.tenko.app.ui.components

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tenko.app.R
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet

@Composable
fun ChatInput(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent
    )
    val context = LocalContext.current

    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                2.dp,
                Tekhelet,
                RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Escribe tu mensaje...") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (text.isBlank()) {
                        Toast.makeText(
                            context,
                            "No puedes enviar un mensaje vacío",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        onSend(text.trim())
                        text = ""
                    }
                }
            ),
            maxLines = 4,
            colors = colors
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                if (text.isBlank()) {
                    Toast.makeText(context, "No puedes enviar un mensaje vacío", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    onSend(text.trim())
                    text = ""
                }
            },
            content = {
                Icon(
                    painter = painterResource(R.drawable.paper_plane_regular_full),
                    contentDescription = "Enviar",
                    tint = Tekhelet,
                    modifier = Modifier.size(26.dp)
                )
            }
        )
    }
}

@Composable
fun ChatInput(
    onSend: (String) -> Unit,
    enableWhiteSpace: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent
    )
    val context = LocalContext.current

    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                2.dp,
                Tekhelet,
                RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { newText ->
                if (!newText.contains(" ") && newText.length <= 10)
                    text = newText.split(" ")
                        .joinToString(" ") { word ->
                            word.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase() else it.toString()
                            }
                        }
            },
            placeholder = { Text("Escribe tu mensaje...") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (text.isBlank()) {
                        Toast.makeText(
                            context,
                            "No puedes enviar un mensaje vacío",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        onSend(text.trim())
                        text = ""
                    }
                }
            ),
            singleLine = true,
            colors = colors
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                if (text.isBlank()) {
                    Toast.makeText(context, "No puedes enviar un mensaje vacío", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    onSend(text.trim())
                    text = ""
                }
            },
            content = {
                Icon(
                    painter = painterResource(R.drawable.paper_plane_regular_full),
                    contentDescription = "Enviar",
                    tint = Tekhelet,
                    modifier = Modifier.size(26.dp)
                )
            }
        )
    }
}

@Composable
fun ChatInput(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier,
    isNumeric: Boolean
) {
    val colors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent
    )
    val context = LocalContext.current

    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                2.dp,
                Tekhelet,
                RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { newText ->
                if (newText.all { it.isDigit() && !it.isWhitespace() }) {
                    text = newText
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (text.isBlank()) {
                        onSend("0")
                        text = ""
                        return@KeyboardActions
                    } else if (text.toInt() > 20) {
                        Toast.makeText(
                            context,
                            "El valor no puede ser mayor a 20",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return@KeyboardActions
                    }
                    onSend(text)
                    text = ""
                }
            ),
            placeholder = { Text("Digite la cantidad...") },
            supportingText = {
                if (text.isBlank()) Text(
                    "Si no aplica, deje en blanco o escriba 0",
                    color = SweetGrey
                )
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = colors
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                if (text.isBlank()) {
                    onSend("0")
                    text = ""
                    return@IconButton
                } else if (text.toInt() > 20) {
                    Toast.makeText(context, "El valor no puede ser mayor a 20", Toast.LENGTH_SHORT)
                        .show()
                    return@IconButton
                }
                onSend(text)
                text = ""
            },
            content = {
                Icon(
                    painter = painterResource(R.drawable.paper_plane_regular_full),
                    contentDescription = "Enviar",
                    tint = Tekhelet,
                    modifier = Modifier.size(26.dp)
                )
            }
        )
    }
}