package com.tenko.app.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.time.LocalDate

class MedicationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Toma tu medicamento"
        val startDateStr = intent.getStringExtra("startDate") // yyyy-MM-dd
        val endDateStr = intent.getStringExtra("endDate") // yyyy-MM-dd

        // Validar si hoy todavía está dentro del rango
        val today = LocalDate.now()
        val endDate = LocalDate.parse(endDateStr)
        val startDate = LocalDate.parse(startDateStr)

        if (!today.isAfter(endDate)) {
            showNotification(context, title)
            // Si el tratamiento sigue, podrías reprogramar aquí
            // o usar una alarma repetitiva.
        }
    }

    private fun showNotification(context: Context, title: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medication_channel"

        val channel = NotificationChannel(channelId, "Recordatorios", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(com.tenko.app.R.drawable.tenko_avatar) // Asegúrate de tener un icono
            .setContentTitle(title)
            .setContentText("Es hora de tu dosis programada.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
