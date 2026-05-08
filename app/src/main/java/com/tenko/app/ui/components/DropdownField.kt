package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.data.model.Country
import com.tenko.app.data.model.Speciality
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.White
import kotlin.text.contains

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialityDropdown(
    selected: String,
    onSelected: (Speciality) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val options = Speciality.entries.toTypedArray()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {  },
            readOnly = true,
            placeholder = { Text("Selecciona especialidad") },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.chevron_down_solid_full),
                    contentDescription = "Dropdown icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedBorderColor = PompAndPower,
                unfocusedBorderColor = SweetGrey,
                focusedTrailingIconColor = PompAndPower,
                unfocusedTrailingIconColor = SweetGrey,
                focusedTextColor = PompAndPower,
                unfocusedTextColor = SweetGrey,
                unfocusedPlaceholderColor = SweetGrey
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxHeight(0.6f),
            containerColor = White,
            shape = RoundedCornerShape(12.dp),
        ) {
            options.forEach { speciality ->
                DropdownMenuItem(
                    text = { Text(speciality.displayName) },
                    onClick = {
                        onSelected(speciality)
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

@Composable
fun CountryDropdown(
    countries: List<Country>,
    selected: Country,
    onSelect: (Country) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    val filtered = countries.filter {
        it.name.contains(search, true) || it.code.contains(search)
    }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxHeight(),
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                disabledContainerColor = White,
                disabledContentColor = RaisinBlack,
            ),
            border = ButtonDefaults.outlinedButtonBorder(true),
            content = {
                Text(
                    text = "${selected.flag} ${selected.code}",
                    fontSize = 16.sp
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = White,
            shape = RoundedCornerShape(12.dp),
            content = {
                Column(
                    modifier = Modifier
                        .height(450.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    filtered.forEach { country ->
                        DropdownMenuItem(
                            text = {
                                Text("${country.flag} ${country.name} (${country.code})")
                            },
                            onClick = {
                                onSelect(country)
                                expanded = false
                            }
                        )
                    }
                }
            }
        )
    }
}