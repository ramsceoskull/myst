package com.tenko.myst.data.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.util.Calendar

fun scheduleMedicationAlarm(context: Context, startDate: LocalDate?, endDate: LocalDate?, hour: Int, minute: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MedicationReceiver::class.java).apply {
        putExtra("title", "Hora de tu medicamento")
        putExtra("startDate", startDate.toString())
        putExtra("endDate", endDate.toString())
    }

    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    // Ejemplo: Programar para mañana a las 8:00 AM
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        // Si hoy es después de la fecha de inicio, empezar mañana
        // Si no, empezar en la fecha de inicio
    }

    // Alarmas exactas (requieren permiso SCHEDULE_EXACT_ALARM en Android 12+)
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY, // Se repite cada día
        pendingIntent
    )
}