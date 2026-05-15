package com.tenko.app.navigation

sealed class AppScreens(val route: String) {
    object TermsScreen : AppScreens("terms_screen")
    object PrivacyPolicyScreen : AppScreens("privacy_policy_screen")
    object MainScreen : AppScreens("main_screen")
    object AddMedicationScreen : AppScreens("add_medication_screen")
    object AddAppointmentScreen : AppScreens("add_appointment_screen/{doctorId}") {
        fun createRoute(doctorId: Int) = "add_appointment_screen/$doctorId"
    }

    object SplashScreen : AppScreens("splash_screen")
    object LoginScreen : AppScreens("login_screen")
    object SignupScreen : AppScreens("signup_screen")
    object ProfileScreen : AppScreens("profile_screen")
    object ReportsScreen : AppScreens("reports_screen")
    object ClinicalHistoryScreen : AppScreens("clinical_history_screen")
    object UpdateProfileScreen : AppScreens("update_profile_screen")
    object ChatScreen : AppScreens("chat_screen")
    object CalendarScreen : AppScreens("calendar_screen")
    object LaboratoryStudiesScreen : AppScreens("lab_studies_screen")
    object ForgotPasswordScreen : AppScreens("forgot_password_screen/{emailId}") {
        fun createRoute(emailId: String) = "forgot_password_screen/$emailId"
    }

    object ValidateEmailScreen : AppScreens("validate_email_screen/{emailId}") {
        fun createRoute(emailId: String) = "validate_email_screen/$emailId"
    }

    //    object EmailSentScreen : AppScreens("email_sent_screen")
    object NotificationsOverlay : AppScreens("notifications_overlay")
    object DoctorsScreen : AppScreens("doctors_screen")
    object AddDoctorScreen : AppScreens("add_doctor_contact_screen")
    object DoctorDetailsScreen : AppScreens("doctor_details_screen/{doctorId}") {
        fun createRoute(doctorId: Int) = "doctor_details_screen/$doctorId"
    }

    object AllNotificationsScreen : AppScreens("all_notifications_screen")
    object NotificationDetailsScreen : AppScreens("notification_details_screen/{notificationId}") {
        fun createRoute(notificationId: Int) = "notification_details_screen/$notificationId"
    }
}
