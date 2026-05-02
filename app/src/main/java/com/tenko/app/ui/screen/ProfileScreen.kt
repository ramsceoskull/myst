package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.api.TokenManager
import com.tenko.app.data.serializable.UserResponse
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.ActionCard
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.BottomNavigationBar
import com.tenko.app.ui.components.MenuItem
import com.tenko.app.ui.components.ProfilePicture
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val user = authViewModel.currentUser
    val context = LocalContext.current

    val tokenManager = remember { TokenManager(context) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (user == null) {
            authViewModel.getUser(navController)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Perfil",
                onBackClick = { navController.popBackStack() },
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 25.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileSection(user)

            PlanAndInviteSection()

            MenuItem(R.drawable.file_pdf_solid_full, "Historial de reportes", {navController.navigate(AppScreens.ReportsScreen.route)})
            MenuItem(R.drawable.folder_open_solid_full, "Historial clínico", {navController.navigate(AppScreens.ClinicalHistoryScreen.route)})
            MenuItem(R.drawable.circle_question_solid_full, "Ayuda")
            MenuItem(R.drawable.gear_solid_full, "Editar Perfil", {navController.navigate(AppScreens.UpdateProfileScreen.route)})
            MenuItem(R.drawable.door_open_solid_full, "Cerrar sesión", { showDialog = true })

            if(showDialog)
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
                                authViewModel.logout(
                                    tokenManager = tokenManager,
                                    onLogoutComplete = {
                                        navController.navigate(AppScreens.LoginScreen.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = White,
                                containerColor = Tekhelet
                            ),
                            content =  { Text("Sí, salir") }
                        )
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDialog = false },
                            content =  { Text("Cancelar", color = SweetGrey) }
                        )
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.door_open_solid_full),
                                contentDescription = null,
                            )
                            Text("¿Cerrar sesión?")
                        }
                    },
                    text = { Text("Tendrás que ingresar tus credenciales nuevamente para entrar a Myst.") },
                    shape = RoundedCornerShape(12.dp),
                    containerColor = White,
                    titleContentColor = Tekhelet,
                    textContentColor = SweetGrey
                )
        }
    }
}

@Composable
fun ProfileSection(user: UserResponse?) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfilePicture(user?.picture?.toUri(), 200.dp)

        Text(
            text = user?.name ?: "Nombre de Usuario",
            fontSize = 32.sp,
            color = Tekhelet,
            fontWeight = FontWeight.SemiBold,
            fontFamily = StarsLove
        )
    }
}

@Composable
fun PlanAndInviteSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AntiFlashWhite,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionCard(
            icon = Icons.Default.CardMembership,
            title = "Prueba",
            subtitle = "Tipo de plan",
        )

        ActionCard(
            icon = Icons.Default.Favorite,
            title = "Invita",
            subtitle = "A amistades",
            onClick = {  }
        )
    }
}