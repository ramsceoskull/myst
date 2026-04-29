package com.tenko.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuItemColors
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
import androidx.compose.ui.unit.dp
import com.tenko.app.R
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {  },
            readOnly = true,
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.chevron_down_solid_full),
                    contentDescription = "Dropdown icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = PompAndPower,
                focusedBorderColor = PompAndPower,
                unfocusedBorderColor = Color.Transparent,
                focusedTrailingIconColor = PompAndPower,
                unfocusedTrailingIconColor = White,
                focusedTextColor = PompAndPower,
                unfocusedTextColor = White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = White,
            shape = RoundedCornerShape(12.dp),
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    },
                    colors = MenuItemColors(
                        textColor = RaisinBlack,
                        leadingIconColor = Color.Unspecified,
                        trailingIconColor = Color.Unspecified,
                        disabledTextColor = Color.Unspecified,
                        disabledLeadingIconColor = Color.Unspecified,
                        disabledTrailingIconColor = Color.Unspecified,
                    )
                )
            }
        }
    }
}