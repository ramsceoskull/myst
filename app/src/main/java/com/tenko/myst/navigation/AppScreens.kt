package com.tenko.myst.navigation

sealed class AppScreens(val route: String) {
    object MainScreen : AppScreens("main_screen")
    object AddMedicationScreen : AppScreens("add_medication_screen")
    object SplashScreen : AppScreens("splash_screen")
    object LoginScreen : AppScreens("login_screen")
    object SignupScreen : AppScreens("signup_screen")
    object ProfileScreen : AppScreens("profile_screen")
    object UpdateProfileScreen : AppScreens("update_profile_screen")
    object ChatScreen : AppScreens("chat_screen")
    object CalendarScreen : AppScreens("calendar_screen")
    object ForgotPasswordScreen : AppScreens("forgot_password_screen")
    object ValidateEmailScreen : AppScreens("validate_email_screen")
    object EmailSentScreen : AppScreens("email_sent_screen")
    object NotificationsOverlay: AppScreens("notifications_overlay")

    object DoctorsScreen : AppScreens("doctors_screen")
    object DoctorDetailsScreen : AppScreens("doctor_details_screen/{doctorId}")

    object AllNotificationsScreen : AppScreens("all_notifications_screen")
    object NotificationDetailsScreen : AppScreens("notification_details_screen/{notificationId}")/* {
        fun createRoute(notificationId: Int) = "notification_details_screen/$notificationId"
    }*/
}
