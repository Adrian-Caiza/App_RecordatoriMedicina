package com.example.medicationtracker.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.medicationtracker.data.local.MedicationEntity
import java.util.Calendar

object MedicineScheduler {

    fun programarAlarma(context: Context, item: MedicationEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Pasamos los datos de la medicina al Receiver para mostrarlos en la notificación
        val intent = Intent(context, MedicineReceiver::class.java).apply {
            putExtra("MED_ID", item.id)
            putExtra("MED_NOMBRE", item.nombre)
            putExtra("MED_DOSIS", item.dosis)
        }

        // Usamos item.id como requestCode para que cada medicina sea única
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendario = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, item.hora)
            set(Calendar.MINUTE, item.minuto)
            set(Calendar.SECOND, 0)
        }

        // Si la hora ya pasó hoy, programar para mañana
        if (calendario.timeInMillis <= System.currentTimeMillis()) {
            calendario.add(Calendar.DAY_OF_MONTH, 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendario.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendario.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelarAlarma(context: Context, item: MedicationEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MedicineReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id, // Importante: usar el mismo ID
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}