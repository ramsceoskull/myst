package com.tenko.app.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tenko.app.data.api.TokenManager
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.data.view.MedicineViewModel
import com.tenko.app.data.view.NotificationViewModel
import com.tenko.app.ui.components.NotificationsOverlay
import com.tenko.app.ui.screen.AddDoctorScreen
import com.tenko.app.ui.screen.AddMedicationScreen
import com.tenko.app.ui.screen.AllNotificationsScreen
import com.tenko.app.ui.screen.CalendarScreen
import com.tenko.app.ui.screen.ChatScreen
import com.tenko.app.ui.screen.ClinicalHistoryScreen
import com.tenko.app.ui.screen.DoctorsScreen
import com.tenko.app.ui.screen.EmailSentScreen
import com.tenko.app.ui.screen.LoginScreen
import com.tenko.app.ui.screen.MainScreen
import com.tenko.app.ui.screen.NotificationDetailScreen
import com.tenko.app.ui.screen.PdfViewerScreen
import com.tenko.app.ui.screen.ProfileScreen
import com.tenko.app.ui.screen.ReportsScreen
import com.tenko.app.ui.screen.SignupScreen
import com.tenko.app.ui.screen.SplashScreen
import com.tenko.app.ui.screen.UpdateProfileScreen

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val notificationViewModel = viewModel<NotificationViewModel>()
    val medicineViewModel = viewModel<MedicineViewModel>()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.initAuth(tokenManager)
    }

    val isChecking = authViewModel.isCheckingAuth
    val currentUser = authViewModel.currentUser

    // Observamos el token del DataStore
    val token by tokenManager.getToken.collectAsState(initial = "loading")

    if(isChecking || token == "loading") {
        SplashScreen()
    } else {
        val startTrigger = if(token == null || currentUser == null) AppScreens.LoginScreen.route else AppScreens.MainScreen.route

        NavHost(
            navController = navController,
            startDestination = startTrigger
        ) {
            composable(AppScreens.SplashScreen.route) { SplashScreen() }
            composable(AppScreens.SignupScreen.route) { SignupScreen(navController, authViewModel) }
            composable(AppScreens.LoginScreen.route) { LoginScreen(navController, authViewModel) }
            composable(AppScreens.ProfileScreen.route) { ProfileScreen(navController, authViewModel) }
            composable(AppScreens.ReportsScreen.route) { ReportsScreen(navController, authViewModel) }
            composable(AppScreens.ClinicalHistoryScreen.route) { ClinicalHistoryScreen(navController, authViewModel) }
            composable(AppScreens.UpdateProfileScreen.route) { UpdateProfileScreen(navController, authViewModel, tokenManager) }
            composable(AppScreens.ChatScreen.route) { ChatScreen(navController) }
            composable(AppScreens.CalendarScreen.route) { CalendarScreen(navController) }
            composable(AppScreens.DoctorsScreen.route) { DoctorsScreen(navController) }
            composable(AppScreens.AddMedicationScreen.route) {
                AddMedicationScreen(
                    viewModel = medicineViewModel,
                    onClose = { navController.popBackStack() },
                )
            }
            composable(AppScreens.MainScreen.route) { MainScreen(navController, authViewModel, notificationViewModel, medicineViewModel) }
            composable("terms") {
                PdfViewerScreen(
                    pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                    onAccept = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("termsAccepted", true)

                        navController.popBackStack()
                    },
                    onDismiss = {
                        navController.popBackStack()
                    }
                )
            }
            composable(AppScreens.NotificationsOverlay.route) {
                NotificationsOverlay(
                    viewModel = notificationViewModel,
                    onDismiss = { mutableStateOf(false) },
                    onSeeAllClick = { navController.navigate(AppScreens.AllNotificationsScreen.route) },
                    onNotificationClick = {
                        navController.navigate("${AppScreens.NotificationDetailsScreen.route}/${it.id}")
                    }
                )
            }
            composable(AppScreens.AllNotificationsScreen.route) {
                AllNotificationsScreen(
                    viewModel = notificationViewModel,
                    onBack = { navController.popBackStack() },
                    onNotificationClick = {
                        navController.navigate("${AppScreens.NotificationDetailsScreen.route}/${it.id}")
                    }
                )
            }
            composable(
                route = AppScreens.NotificationDetailsScreen.route,
                arguments = listOf(
                    navArgument("notificationId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val notificationId = backStackEntry.arguments?.getInt("notificationId")
                val notification = notificationViewModel.notifications.first { it.id == notificationId }
                NotificationDetailScreen(notification)
            }
            composable(AppScreens.ForgotPasswordScreen.route) {
                EmailSentScreen(
                    title = "Se te ha enviado un correo",
                    description = "Favor de revisar tu bandeja de entrada o tu carpeta de spam para restablecer tu contraseña",
                    actionLabel = "Iniciar Sesión",
                    onClick = { navController.navigate(AppScreens.LoginScreen.route) },
                    onResendClick = { /* Lógica para reenviar el correo */ }
                )
            }
            composable(AppScreens.ValidateEmailScreen.route) {
                EmailSentScreen(
                    title = "Confirma tu correo electrónico",
                    description = "Hemos enviado un correo de confirmación a tu bandeja de entrada. Por favor, revisa tu bandeja de entrada o tu carpeta de spam para activar tu cuenta.",
                    actionLabel = "Abrir correo",
                    onClick = {
                        val intent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_APP_EMAIL)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }

                        if(intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            // Si no hay una app de correo, simplemente volvemos al login
                            navController.navigate(AppScreens.LoginScreen.route)
                        }
                    },
                    onResendClick = { /* Lógica para reenviar el correo */ }
                )
            }
            composable(AppScreens.AddDoctorScreen.route){
                AddDoctorScreen(onBackClick = { navController.popBackStack() })
            }
            /*composable(
                route = AppScreens.DoctorDetailsScreen.route,
                arguments = listOf(
                    navArgument("doctorId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getInt("doctorId")
                val doctor = doctorsList.find { it.id == doctorId }

                doctor?.let {
                    DoctorDetailsScreen(navController, it)
                }
            }*/
        }
    }
}
