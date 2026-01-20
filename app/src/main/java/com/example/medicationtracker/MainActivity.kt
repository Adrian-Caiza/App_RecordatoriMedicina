package com.example.medicationtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.medicationtracker.data.local.AppDatabase
import com.example.medicationtracker.ui.MedicationScreen
import com.example.medicationtracker.ui.MedicationViewModel
import com.example.medicationtracker.ui.MedicationViewModelFactory
import com.example.medicationtracker.ui.theme.MedicationtrackerTheme // Tu tema

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crearCanalNotificaciones()

        // DB Setup (Simplificado para el ejemplo)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "medicina-db"
        ).build()

        val factory = MedicationViewModelFactory(db.medicationDao(), applicationContext)

        setContent {
            MedicationtrackerTheme {
                val viewModel: MedicationViewModel = viewModel(factory = factory)
                MedicationScreen(viewModel)
            }
        }
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "med_channel_id",
                "Recordatorios de Medicina",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para tomar medicamentos"
                enableVibration(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}