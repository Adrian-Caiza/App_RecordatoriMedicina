package com.example.medicationtracker.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medicationtracker.alarm.MedicineScheduler
import com.example.medicationtracker.data.local.MedicationDao
import com.example.medicationtracker.data.local.MedicationEntity
import com.example.medicationtracker.data.local.NotificationPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MedicationViewModel(
    private val dao: MedicationDao,
    private val context: Context
) : ViewModel() {

    val medicinas = dao.obtenerTodas()

    // Estados del formulario
    private val _nombre = MutableStateFlow("")
    val nombre = _nombre.asStateFlow()

    private val _dosis = MutableStateFlow("")
    val dosis = _dosis.asStateFlow()

    private val _hora = MutableStateFlow(8)
    val hora = _hora.asStateFlow()

    private val _minuto = MutableStateFlow(0)
    val minuto = _minuto.asStateFlow()

    // Estado para saber si estamos editando (null = creando nuevo)
    private var idEnEdicion: Int? = null
    private val _esEdicion = MutableStateFlow(false)
    val esEdicion = _esEdicion.asStateFlow()

    // Estado del Switch Global
    private val _notificacionesActivas = MutableStateFlow(NotificationPreferences.getNotificacionesGlobales(context))
    val notificacionesActivas = _notificacionesActivas.asStateFlow()

    fun actualizarCampos(n: String, d: String, h: Int, m: Int) {
        _nombre.value = n
        _dosis.value = d
        _hora.value = h
        _minuto.value = m
    }

    // --- LÓGICA DE EDICIÓN ---
    fun cargarParaEditar(item: MedicationEntity) {
        idEnEdicion = item.id
        _nombre.value = item.nombre
        _dosis.value = item.dosis
        _hora.value = item.hora
        _minuto.value = item.minuto
        _esEdicion.value = true
    }

    fun cancelarEdicion() {
        limpiarFormulario()
    }

    private fun limpiarFormulario() {
        idEnEdicion = null
        _esEdicion.value = false
        _nombre.value = ""
        _dosis.value = ""
        // No reseteamos la hora para comodidad del usuario
    }

    // --- GUARDAR (CREAR O ACTUALIZAR) ---
    fun guardarMedicamento() {
        if (_nombre.value.isBlank()) return

        viewModelScope.launch {
            val notificacionesHabilitadas = _notificacionesActivas.value

            if (idEnEdicion != null) {
                // ACTUALIZAR
                val medActualizada = MedicationEntity(
                    id = idEnEdicion!!,
                    nombre = _nombre.value,
                    dosis = _dosis.value,
                    hora = _hora.value,
                    minuto = _minuto.value
                )
                dao.actualizar(medActualizada)

                // Solo reprogramamos la alarma si las notificaciones globales están activas
                if (notificacionesHabilitadas) {
                    MedicineScheduler.programarAlarma(context, medActualizada)
                } else {
                    MedicineScheduler.cancelarAlarma(context, medActualizada)
                }

            } else {
                // CREAR NUEVO
                val nuevaMed = MedicationEntity(
                    nombre = _nombre.value,
                    dosis = _dosis.value,
                    hora = _hora.value,
                    minuto = _minuto.value
                )
                val id = dao.insertar(nuevaMed)
                val medConId = nuevaMed.copy(id = id.toInt())

                if (notificacionesHabilitadas) {
                    MedicineScheduler.programarAlarma(context, medConId)
                }
            }
            limpiarFormulario()
        }
    }

    fun eliminarMedicamento(item: MedicationEntity) {
        viewModelScope.launch {
            MedicineScheduler.cancelarAlarma(context, item)
            dao.eliminar(item)
            if (idEnEdicion == item.id) limpiarFormulario()
        }
    }

    // --- LÓGICA DE INTERRUPTOR GLOBAL ---
    fun toggleNotificacionesGlobales(activar: Boolean) {
        _notificacionesActivas.value = activar
        NotificationPreferences.setNotificacionesGlobales(context, activar)

        viewModelScope.launch {
            // Obtenemos la lista actual de la base de datos (una sola vez)
            val listaActual = medicinas.first()

            if (activar) {
                // Reactivar todas las alarmas
                listaActual.forEach { med ->
                    MedicineScheduler.programarAlarma(context, med)
                }
            } else {
                // Cancelar todas las alarmas
                listaActual.forEach { med ->
                    MedicineScheduler.cancelarAlarma(context, med)
                }
            }
        }
    }
}

class MedicationViewModelFactory(private val dao: MedicationDao, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MedicationViewModel(dao, context) as T
    }
}