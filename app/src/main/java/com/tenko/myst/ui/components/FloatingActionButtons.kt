package com.tenko.myst.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.myst.R
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.White

@Composable
fun AddMedicationButton() {
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

@Composable
fun AddContactButton() {
    FloatingActionButton(
        onClick = { /*registerDoctor()*/ },
        containerColor = PompAndPower,
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                tint = White,
                contentDescription = "Registar contacto",
                painter = painterResource(R.drawable.address_book_solid_full),
                modifier = Modifier.size(30.dp)
            )

            Text(
                text = "Agregar",
                fontSize = 14.sp,
                color = White,
                textAlign = TextAlign.Center
            )
        }
    }
}