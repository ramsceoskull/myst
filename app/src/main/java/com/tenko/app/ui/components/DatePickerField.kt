package com.tenko.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tenko.app.R
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.White

@Composable
fun DatePickerField(
    label: String,
    value: String,
    colors: TextFieldColors,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 66.dp),
            enabled = false,
            placeholder = { Text(label) },
            trailingIcon = {
                if (value.isEmpty())
                    Icon(
                        painter = painterResource(R.drawable.arrow_right_solid_full),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = colors,
        )

        IconButton(
            onClick = { },
            modifier = Modifier.size(66.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = PompAndPower,
                contentColor = White
            ),
            shape = RoundedCornerShape(12.dp),
            content = {
                Icon(
                    painter = painterResource(R.drawable.calendar_days_regular_full),
                    contentDescription = "Calendar icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                )
            }
        )
    }
}