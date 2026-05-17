package com.tenko.app.data.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tenko.app.R

class MedicationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        createNotificationChannel(context)

        val medicationName =
            intent.getStringExtra("medication_name") ?: "Medicamento"

        val notification = NotificationCompat.Builder(context, "medication_channel")
            .setSmallIcon(R.drawable.alarm_clock_solid_full)
            .setContentTitle("Hora de tu medicamento")
            .setContentText("Es momento de tomar: $medicationName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            "medication_channel",
            "Medicamentos",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Recordatorios de medicamentos"
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        manager.createNotificationChannel(channel)
    }
}
