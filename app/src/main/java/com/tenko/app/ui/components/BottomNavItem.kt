package com.tenko.app.ui.components

import com.tenko.app.R
import com.tenko.app.navigation.AppScreens

sealed class BottomNavItem(
    val route: String,
    val icon: Int,
    val label: String
) {
    object Calendar : BottomNavItem(AppScreens.CalendarScreen.route, R.drawable.calendar_regular_full, "Calendario")
    object Home : BottomNavItem(AppScreens.MainScreen.route, R.drawable.house_regular_full, "Home")
    object Chat : BottomNavItem(AppScreens.ChatScreen.route, R.drawable.comment_regular_full, "Chat")
}