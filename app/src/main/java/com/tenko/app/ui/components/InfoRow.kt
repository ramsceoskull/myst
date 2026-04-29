package com.tenko.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import com.tenko.app.ui.theme.RaisinBlack

@Composable
fun InfoRow(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = RaisinBlack
                )

                if(onClick != null) {
                    Icon(
                        painter = painterResource(R.drawable.chevron_right_solid_full),
                        contentDescription = "Editar $label",
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp).padding(start = 8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(30.dp))
                }
            }
        }

        HorizontalDivider(
            color = Color(0xFFE0E0E0),
            thickness = (0.7).dp
        )
    }
}

@Composable
fun DeleteAccountRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(R.drawable.trash_can_regular_full),
            contentDescription = "Eliminar cuenta",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
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