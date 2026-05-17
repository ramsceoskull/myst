package com.tenko.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tenko.app.R
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.RaisinBlack

@Composable
fun InfoRow(
    label: String,
    value: String,
    showInput: Boolean = false,
    onClick: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null,
    input: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = value,
            color = RaisinBlack,
            style = MaterialTheme.typography.bodyMedium
        )

        if (onClick != null) {
            Icon(
                painter = painterResource(R.drawable.chevron_right_solid_full),
                contentDescription = "Editar $label",
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 8.dp),
                tint = Color.Gray
            )
        } else
            Spacer(modifier = Modifier.size(30.dp))
    }

    if (input != null)
        AnimatedEditableInput(
            visible = showInput,
            onCancel = onCancel ?: {},
            onDone = onDone ?: {},
            label = label,
            input = input
        )

    HorizontalDivider(
        color = AntiFlashWhite,
        thickness = 1.dp
    )
}

@Composable
fun DeleteAccountRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        Icon(
            painter = painterResource(R.drawable.trash_can_regular_full),
            contentDescription = "Eliminar cuenta",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InfoRowPreview() {
    InfoRow(
        label = "Correo electrónico",
        value = "a22310355@ceti.mx",
        onClick = { }
    )
}