package com.example.medicationtracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamentos")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val dosis: String, // Ej: "1 pastilla", "5ml"
    val hora: Int,     // Formato 24h
    val minuto: Int
)