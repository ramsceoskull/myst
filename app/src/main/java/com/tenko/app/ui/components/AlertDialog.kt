package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tenko.app.R
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    title: Pair<Int, String>
    = Pair(R.drawable.bluesky_brands_solid_full, "Título del diálogo"),
    confirmButton: Pair<String, () -> Unit>,
    text: @Composable () -> Unit,
    dismissButtonText: String = "Cancelar"
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = confirmButton.second,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = White,
                    containerColor = Tekhelet
                ),
                content = {
                    Text(confirmButton.first)
                }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                content = {
                    Text(
                        text = dismissButtonText,
                        color = SweetGrey
                    )
                }
            )
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(title.first),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Text(title.second)
            }
        },
        text = {
            Column() {
                text()
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = White,
        titleContentColor = Tekhelet,
        textContentColor = SweetGrey
    )
}