package com.example.medicationtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MedicationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
}