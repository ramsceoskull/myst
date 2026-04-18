package com.tenko.myst.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tenko.myst.R
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.White

@Composable
fun AddMedicationButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = PompAndPower
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.red_and_white_pills),
                contentDescription = "Medication Icon",
                tint = null,
                modifier = Modifier.size(40.dp)
            )
            Icon(
                painter = painterResource(R.drawable.plus_solid_full),
                contentDescription = "Agregar medicación",
                modifier = Modifier.size(25.dp),
                tint = White
            )
        }
    }
}

@Composable
fun AddContactButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = PompAndPower
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.address_book_solid_full),
                contentDescription = "Address book Icon",
                tint = White,
                modifier = Modifier.size(40.dp)
            )
            Icon(
                painter = painterResource(R.drawable.plus_solid_full),
                contentDescription = "Agregar contacto",
                modifier = Modifier.size(25.dp),
                tint = White
            )
        }
    }
}

@Composable
fun AddCalendarEvent(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = PompAndPower
    ) {
        Icon(
            painter = painterResource(R.drawable.plus_solid_full),
            contentDescription = "Registrar síntoma",
            Modifier.size(24.dp),
            tint = White
        )
    }
}