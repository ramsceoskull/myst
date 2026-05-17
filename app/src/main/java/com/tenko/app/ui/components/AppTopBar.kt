package com.tenko.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: Triple<() -> Unit, Int, String> = Triple({}, 0, "")
) {
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, AntiFlashWhite)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    color = Tekhelet,
                    fontFamily = StarsLove,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    modifier = Modifier.offset(y = 4.dp)
                )
            },
            navigationIcon = {
                onBackClick?.let { callback ->
                    var lastClickTime by remember { mutableLongStateOf(0L) }
                    IconButton(
                        onClick = {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastClickTime > 1000L) { // 1 second debounce
                                lastClickTime = currentTime
                                callback()
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(R.drawable.chevron_left_solid_full),
                            contentDescription = "Back",
                            tint = Tekhelet
                        )
                    }
                }
            },
            actions = {
                if (actions.second != 0) {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(id = actions.second),
                            contentDescription = null,
                            tint = Tekhelet
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 8.dp)
        )

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            actions.first()
                            showDialog = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = White,
                            containerColor = Tekhelet
                        ),
                        content = { Text("Confirmar") }
                    )
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false },
                        content = { Text("Cancelar", color = SweetGrey) }
                    )
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(actions.second),
                            contentDescription = null,
                        )
                        Text("¿Estás segura?")
                    }
                },
                text = {
                    Text(actions.third)
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = White,
                titleContentColor = Tekhelet,
                textContentColor = SweetGrey
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    actions: (() -> Unit)? = null
) {
    // 1. Obtenemos el usuario del estado del ViewModel (es reactivo)
    val user = authViewModel.currentUser

    // 2. Si por alguna razón el usuario es nulo (ej. refresco manual),
    // lo pedimos una SOLA VEZ al entrar.
    LaunchedEffect(Unit) {
        if (user == null) {
            authViewModel.getUser(navController)
        }
    }

    Surface(
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, AntiFlashWhite)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    color = Tekhelet,
                    fontFamily = StarsLove,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    modifier = Modifier.offset(y = 4.dp)
                )
            },
            navigationIcon = {
                var lastClickTime by remember { mutableLongStateOf(0L) }
                IconButton(
                    onClick = {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastClickTime > 1000L) {
                            lastClickTime = currentTime
                            navController.navigate(AppScreens.ProfileScreen.route)
                        }
                    },
                    content = { ProfilePicture(user?.picture?.toUri(), 40.dp, false) }
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 8.dp)
        )
    }
}
