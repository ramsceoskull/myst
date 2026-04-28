package com.tenko.app.data.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tenko.app.data.model.NotificationItem

class NotificationViewModel : ViewModel() {
    private val _notifications = mutableStateListOf(
        NotificationItem(1, "Radouane Khiri", "You are almost out of premium data", false),
        NotificationItem(2, "Mom's line", "Congratulations!", true),
        NotificationItem(3, "Family Pool", "Payment reminder", false),
        NotificationItem(4, "Dad's line", "You are almost out", false),
        NotificationItem(5, "Health Reminder", "Time to log your symptoms", false)
    )

    val notifications: List<NotificationItem> = _notifications

    val unreadCount: Int
        get() = _notifications.count { !it.isRead }

    val hasUnread: Boolean
        get() = unreadCount > 0

    var filterUnread by mutableStateOf(false)
        private set

    fun toggleFilter(unread: Boolean) {
        filterUnread = unread
    }

    fun markAllAsRead() {
        _notifications.replaceAll { it.copy(isRead = true) }
    }

    fun markAsRead(id: Int) {
        val index = _notifications.indexOfFirst { it.id == id }
        if (index != -1) {
            _notifications[index] = _notifications[index].copy(isRead = true)
        }
    }

    fun getFilteredNotifications(): List<NotificationItem> {
        return if (filterUnread) {
            _notifications.filter { !it.isRead }
        } else {
            _notifications
        }
    }
}