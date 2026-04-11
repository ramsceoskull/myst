package com.tenko.myst.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tenko.myst.R
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.White

@Composable
fun DatePickerField(
    label: String,
    value: String,
    colors: TextFieldColors,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            placeholder = { Text(label) },
            colors = colors,
            readOnly = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
        )
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(52.dp).background(color = PompAndPower, shape = RoundedCornerShape(12.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_days_regular_full),
                    contentDescription = "Icono de calendario",
                    tint = White,
                    modifier = Modifier.size(25.dp)
                )
                Icon(
                    painter = painterResource(R.drawable.plus_solid_full),
                    contentDescription = "Icono de agregar",
                    tint = White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}