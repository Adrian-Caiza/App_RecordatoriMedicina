package com.example.medicationtracker.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medicationtracker.alarm.MedicineScheduler
import com.example.medicationtracker.data.local.MedicationDao
import com.example.medicationtracker.data.local.MedicationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicationViewModel(
    private val dao: MedicationDao,
    private val context: Context // Necesitamos contexto para las alarmas
) : ViewModel() {

    val medicinas = dao.obtenerTodas()

    private val _nombre = MutableStateFlow("")
    val nombre = _nombre.asStateFlow()

    private val _dosis = MutableStateFlow("")
    val dosis = _dosis.asStateFlow()

    private val _hora = MutableStateFlow(8)
    val hora = _hora.asStateFlow()

    private val _minuto = MutableStateFlow(0)
    val minuto = _minuto.asStateFlow()

    fun actualizarCampos(n: String, d: String, h: Int, m: Int) {
        _nombre.value = n
        _dosis.value = d
        _hora.value = h
        _minuto.value = m
    }

    fun guardarMedicamento() {
        if (_nombre.value.isBlank()) return

        viewModelScope.launch {
            val nuevaMed = MedicationEntity(
                nombre = _nombre.value,
                dosis = _dosis.value,
                hora = _hora.value,
                minuto = _minuto.value
            )
            // 1. Guardar en BD y obtener el ID generado
            val id = dao.insertar(nuevaMed)

            // 2. Programar alarma con el ID real
            val medConId = nuevaMed.copy(id = id.toInt())
            MedicineScheduler.programarAlarma(context, medConId)

            // Limpiar
            _nombre.value = ""
            _dosis.value = ""
        }
    }

    fun eliminarMedicamento(item: MedicationEntity) {
        viewModelScope.launch {
            // 1. Cancelar alarma primero
            MedicineScheduler.cancelarAlarma(context, item)
            // 2. Borrar de BD
            dao.eliminar(item)
        }
    }
}

// Factory
class MedicationViewModelFactory(private val dao: MedicationDao, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MedicationViewModel(dao, context) as T
    }
}