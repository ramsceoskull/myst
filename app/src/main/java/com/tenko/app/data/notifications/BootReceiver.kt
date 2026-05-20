package com.tenko.app.data.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tenko.app.data.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {

            CoroutineScope(Dispatchers.IO).launch {
                // 1. Instanciamos el DAO directamente usando el Context del sistema
                val reminderDao = AppDatabase.getDatabase(context).reminderDao()

                // 2. Extraemos los medicamentos guardados antes de que se apagara el celular
                val recordatoriosLocal = reminderDao.getAllActiveReminders()

                // 3. Volvemos a registrar las alarmas exactas en el AlarmManager
                recordatoriosLocal.forEach { reminder ->
                    // Aquí invocas el código de tu NotificationPermissionHelper o tu AlarmManager
                    // para reprogramar la alarma usando los datos de 'reminder'
                    // NotificationPermissionHelper.scheduleAlarm(context, reminder)
                }
            }
        }
    }
}