package com.example.medicationtracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medicamentos ORDER BY hora ASC, minuto ASC")
    fun obtenerTodas(): Flow<List<MedicationEntity>>

    @Insert
    suspend fun insertar(med: MedicationEntity): Long // Retorna Long para el ID

    @Update
    suspend fun actualizar(med: MedicationEntity)

    @Delete
    suspend fun eliminar(med: MedicationEntity)
}