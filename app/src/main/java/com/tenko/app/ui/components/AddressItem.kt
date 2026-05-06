package com.tenko.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tenko.app.data.model.Address

@Composable
fun AddressItem(
    address: Address,
    onSelect: () -> Unit,
    onEdit: () -> Unit
) {
    val animatedColor by animateColorAsState(
        if (address.isSelected) Color(0xFFEDE7F6) else Color.Transparent,
        label = "addressSelectionAnimation"
    )

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(animatedColor)
                .clickable { onSelect() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = address.isSelected,
                onClick = onSelect
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(address.name, fontWeight = FontWeight.Bold)
                Text("${address.street}, ${address.city}")
            }

            TextButton(onClick = onEdit) {
                Text("Editar")
            }
        }
    }
}