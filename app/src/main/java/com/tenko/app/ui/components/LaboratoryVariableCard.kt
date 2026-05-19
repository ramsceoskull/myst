package com.tenko.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.data.model.LaboratoryVariable
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.RaisinBlack
import kotlinx.coroutines.CoroutineScope

@Composable
fun LaboratoryVariableCard(
    index: Int,
    variable: LaboratoryVariable,
    scrollState: ScrollState,
    scope: CoroutineScope,
    variableFocus: FocusRequester,
    valueFocus: FocusRequester,
    onVariableChange: (LaboratoryVariable) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (variable.hasError) Color(0xFFFFEBEE) else AntiFlashWhite,
            contentColor = RaisinBlack
        ),
        border = BorderStroke(
            1.dp,
            if (variable.hasError) Color.Red.copy(alpha = 0.4f) else Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onVariableChange(
                            variable.copy(expanded = !variable.expanded)
                        )
                    }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = variable.parameter.ifBlank { "Variable ${index + 1}" },
                    modifier = Modifier.weight(1f),
                    color = if (variable.parameter.isBlank()) Color.Gray else RaisinBlack,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Icon(
                    painter = painterResource(
                        if (variable.expanded) R.drawable.chevron_up_solid_full
                        else R.drawable.chevron_down_solid_full
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (variable.parameter.isBlank()) Color.Gray else RaisinBlack
                )
            }

            AnimatedVisibility(visible = variable.expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FormTextField(
                        type = ContentType.PersonMiddleName,
                        value = variable.parameter,
                        onValueChange = {
                            onVariableChange(
                                variable.copy(
                                    parameter = it,
                                    parameterError = null,
                                    hasError = false
                                )
                            )
                        },
                        label = "Nombre de la variable",
                        placeholder = "Ej: Glucosa, Hemoglobina, etc.",
                        error = variable.parameterError,
                        focusRequester = variableFocus,
                        imeAction = ImeAction.Next,
                        scrollState = scrollState,
                        scope = scope,
                        onNext = { valueFocus.requestFocus() }
                    )

                    AnimatedVisibility(visible = variable.parameter.isNotBlank()) {
                        val units = listOf(
                            "mg/dL",
                            "g/dL",
                            "mmol/L",
                            "UI/L",
                            "mEq/L",
                            "%",
                            "ng/mL",
                            "pg/mL",
                            "cells/µL"
                        )

                        Column {
                            val unit = inputField(
                                value = variable.value,
                                onValueChange = {
                                    onVariableChange(
                                        variable.copy(
                                            value = it,
                                            valueError = null,
                                            hasError = false
                                        )
                                    )
                                },
                                label = Pair(
                                    "Valor numérico",
                                    "Unidad"
                                ),
                                placeholder = "Ej: 85, 13.5, etc.",
                                options = units,
                                error = listOf(variable.valueError, variable.unitError),
                                focusRequester = valueFocus,
                                imeAction = ImeAction.Next,
                                scrollState = scrollState,
                                scope = scope
                            )

                            if (unit.isNotBlank()) {
                                onVariableChange(
                                    variable.copy(
                                        unit = unit,
                                        unitError = null,
                                        hasError = false
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}