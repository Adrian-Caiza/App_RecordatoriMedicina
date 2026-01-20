package com.example.medicationtracker.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicationtracker.data.local.MedicationEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(viewModel: MedicationViewModel) {
    val context = LocalContext.current

    val nombre by viewModel.nombre.collectAsState()
    val dosis by viewModel.dosis.collectAsState()
    val hora by viewModel.hora.collectAsState()
    val minuto by viewModel.minuto.collectAsState()
    val lista by viewModel.medicinas.collectAsState(initial = emptyList())

    val esEdicion by viewModel.esEdicion.collectAsState()
    val notificacionesActivas by viewModel.notificacionesActivas.collectAsState()

    // TimePicker Dialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, h, m -> viewModel.actualizarCampos(nombre, dosis, h, m) },
        hora, minuto, true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Medicinas") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // --- SWITCH GLOBAL ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (notificacionesActivas) "Notificaciones ACTIVAS" else "Notificaciones PAUSADAS",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = notificacionesActivas,
                        onCheckedChange = { viewModel.toggleNotificacionesGlobales(it) }
                    )
                }
            }

            // --- FORMULARIO ---
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (esEdicion) "Editar Medicamento" else "Nuevo Medicamento",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { viewModel.actualizarCampos(it, dosis, hora, minuto) },
                        label = { Text("Medicamento") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.MedicalServices, null) }
                    )

                    OutlinedTextField(
                        value = dosis,
                        onValueChange = { viewModel.actualizarCampos(nombre, it, hora, minuto) },
                        label = { Text("Dosis") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Selector de Hora
                    OutlinedTextField(
                        value = String.format("%02d:%02d", hora, minuto),
                        onValueChange = {},
                        label = { Text("Hora de toma") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { timePickerDialog.show() },
                        leadingIcon = { Icon(Icons.Default.AccessAlarm, null) },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    // Botones de Acción (Guardar / Cancelar)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (esEdicion) {
                            OutlinedButton(
                                onClick = { viewModel.cancelarEdicion() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }
                        }

                        Button(
                            onClick = { viewModel.guardarMedicamento() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (esEdicion) "Actualizar" else "Guardar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Horarios Programados", style = MaterialTheme.typography.titleMedium)

            // --- LISTA ---
            LazyColumn {
                items(lista) { item ->
                    MedicineItem(
                        item = item,
                        onDelete = { viewModel.eliminarMedicamento(item) },
                        onEdit = { viewModel.cargarParaEditar(item) } // <--- Conectamos Editar
                    )
                }
            }
        }
    }
}

@Composable
fun MedicineItem(
    item: MedicationEntity,
    onDelete: () -> Unit,
    onEdit: () -> Unit // <--- Nuevo parámetro
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${item.dosis} • ${String.format("%02d:%02d", item.hora, item.minuto)}", style = MaterialTheme.typography.bodyMedium)
            }

            // Botones de acción
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}