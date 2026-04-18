package com.tenko.myst.navigation

import androidx.compose.runtime.Composable
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
import com.tenko.myst.data.api.TokenManager
import com.tenko.myst.data.view.MedicineViewModel
import com.tenko.myst.data.view.NotificationViewModel
import com.tenko.myst.ui.components.NotificationsOverlay
import com.tenko.myst.ui.screen.AddDoctorScreen
import com.tenko.myst.ui.screen.AddMedicationScreen
import com.tenko.myst.ui.screen.AllNotificationsScreen
import com.tenko.myst.ui.screen.CalendarScreen
import com.tenko.myst.ui.screen.ChatScreen
import com.tenko.myst.ui.screen.DoctorsScreen
import com.tenko.myst.ui.screen.EmailSentScreen
import com.tenko.myst.ui.screen.LoginScreen
import com.tenko.myst.ui.screen.MainScreen
import com.tenko.myst.ui.screen.NotificationDetailScreen
import com.tenko.myst.ui.screen.PdfViewerScreen
import com.tenko.myst.ui.screen.ProfileScreen
import com.tenko.myst.ui.screen.SignupScreen
import com.tenko.myst.ui.screen.SplashScreen
import com.tenko.myst.ui.screen.UpdateProfileScreen

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val notificationViewModel = viewModel<NotificationViewModel>()
    val medicineViewModel = viewModel<MedicineViewModel>()

    val context = LocalContext.current

    // Observamos el token del DataStore
    val token by tokenManager.getToken.collectAsState(initial = "loading")

    when(token) {
        "loading" -> {
            SplashScreen()
        }
        else -> {
            val startTrigger = if(token == null) AppScreens.LoginScreen.route else AppScreens.MainScreen.route

            NavHost(
                navController = navController,
                startDestination = startTrigger
            ) {
                composable(AppScreens.SplashScreen.route) { SplashScreen() }
                composable(AppScreens.SignupScreen.route) { SignupScreen(navController) }
                composable(AppScreens.LoginScreen.route) { LoginScreen(navController) }
                composable(AppScreens.ProfileScreen.route) { ProfileScreen(navController) }
                composable(AppScreens.UpdateProfileScreen.route) { UpdateProfileScreen(navController, tokenManager) }
                composable(AppScreens.ChatScreen.route) { ChatScreen(navController) }
                composable(AppScreens.CalendarScreen.route) { CalendarScreen(navController) }
                composable(AppScreens.DoctorsScreen.route) { DoctorsScreen(navController) }
                composable(AppScreens.AddMedicationScreen.route) {
                    AddMedicationScreen(
                        viewModel = medicineViewModel,
                        onClose = { navController.popBackStack() },
                    )
                }
                composable(AppScreens.MainScreen.route) { MainScreen(navController, notificationViewModel, medicineViewModel) }
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
                composable(
                    route = AppScreens.ValidateEmailScreen.route,
                    arguments = listOf(
                        navArgument("emailId") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("emailId")

                    EmailSentScreen(
                        title = "Confirma tu correo electrónico",
                        description = "Hemos enviado un correo de confirmación a ${email}. Por favor, revisa tu bandeja de entrada o tu carpeta de spam para activar tu cuenta.",
                        actionLabel = "Abrir correo electrónico",
                        onClick = {
                            /*val emailIntent = Intent(Intent.ACTION_VIEW).apply {
                                data = "content://com.android.email.provider".toUri()
                            }

                            if(emailIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(emailIntent)
                            }*/
                            navController.navigate(AppScreens.LoginScreen.route)
                        },
                        onResendClick = { /* Lógica para reenviar el correo */ }
                    )
                }
                composable(AppScreens.AddDoctorScreen.route){
                    AddDoctorScreen(onBack = { navController.popBackStack() })
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
}
