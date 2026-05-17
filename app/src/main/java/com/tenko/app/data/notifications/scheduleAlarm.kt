package com.tenko.app.data.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

fun scheduleMedicationAlarm(
    context: Context,
    medicationName: String,
    endDate: LocalDate,
    time: LocalTime
) {
    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val zoneId = ZoneId.systemDefault()
    var currentDate = LocalDate.now()

    while (!currentDate.isAfter(endDate)) {
        val dateTime = LocalDateTime.of(currentDate, time)

        val triggerMillis = dateTime
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        // Evita programar alarmas pasadas
        if (triggerMillis > System.currentTimeMillis()) {
            val intent = Intent(context, MedicationReceiver::class.java).apply {
                putExtra("medication_name", medicationName)
            }

            val requestCode = (currentDate.toEpochDay().toInt() + time.hashCode())

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        }

        currentDate = currentDate.plusDays(1)
    }
}

fun scheduleAppointmentAlarm(
    context: Context,
    triggerAtMillis: Long,
    title: String,
    description: String
) {
    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            )

            context.startActivity(intent)
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(
            context.let { it as android.app.Activity },
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            100
        )
    }

    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("title", title)
        putExtra("description", description)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        triggerAtMillis.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerAtMillis,
        pendingIntent
    )
}