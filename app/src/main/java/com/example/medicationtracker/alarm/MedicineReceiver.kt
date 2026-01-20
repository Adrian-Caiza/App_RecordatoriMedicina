package com.example.medicationtracker.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.medicationtracker.MainActivity
import com.example.medicationtracker.R

class MedicineReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Recuperar datos
        val nombre = intent.getStringExtra("MED_NOMBRE") ?: "Medicina"
        val dosis = intent.getStringExtra("MED_DOSIS") ?: "Toca tu dosis"
        val id = intent.getIntExtra("MED_ID", 0)

        val contentIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "med_channel_id")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("¡Hora de tu medicina!")
            .setContentText("Tomar: $nombre ($dosis)")
            .setPriority(NotificationCompat.PRIORITY_MAX) // Máxima prioridad para despertar
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Sonido + Vibración
            .build()

        notificationManager.notify(id, notification)
    }
}