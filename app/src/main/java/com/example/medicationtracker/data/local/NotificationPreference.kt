package com.example.medicationtracker.data.local

import android.content.Context
import android.content.SharedPreferences

object NotificationPreferences {
    private const val PREFS_NAME = "med_prefs"
    private const val KEY_GLOBAL_ENABLED = "global_notifications_enabled"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setNotificacionesGlobales(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_GLOBAL_ENABLED, enabled).apply()
    }

    fun getNotificacionesGlobales(context: Context): Boolean {
        // Por defecto true (activadas)
        return getPrefs(context).getBoolean(KEY_GLOBAL_ENABLED, true)
    }
}