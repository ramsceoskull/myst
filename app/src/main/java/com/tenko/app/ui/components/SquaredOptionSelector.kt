package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tenko.app.R
import com.tenko.app.data.model.Genre

@Composable
fun SquaredOptionSelector(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        items(items = options, key = { it }) { option ->
            SquaredOption(
                text = option,
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) }
            )
        }
    }
}

@Composable
fun SquaredOptionSelector(
    options: List<Genre>,
    selectedOption: Genre?,
    onOptionSelected: (Genre) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        items(options, key = { it.name }) { option ->
            SquaredOption(
                text = option.displayName,
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) },
                icon = if (option == Genre.FEMALE) R.drawable.venus_solid_full else R.drawable.mars_solid_full,
            )
        }
    }
}