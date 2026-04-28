package com.tenko.app.data.model

data class NotificationItem(
    val id: Int,
    val title: String = "New Notification",
    val message: String = "You have a new notification.",
    val isRead: Boolean = false
)
