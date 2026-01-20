package com.example.medicationtracker.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(viewModel: MedicationViewModel) {
    val context = LocalContext.current

    val nombre by viewModel.nombre.collectAsState()
    val dosis by viewModel.dosis.collectAsState()
    val hora by viewModel.hora.collectAsState()
    val minuto by viewModel.minuto.collectAsState()
    val lista by viewModel.medicinas.collectAsState(initial = emptyList())

    // TimePicker Dialog
    val calendar = Calendar.getInstance()
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
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()
        ) {
            // Formulario
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nueva Alarma", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { viewModel.actualizarCampos(it, dosis, hora, minuto) },
                        label = { Text("Medicamento (Ej: Ibuprofeno)") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.MedicalServices, null) }
                    )

                    OutlinedTextField(
                        value = dosis,
                        onValueChange = { viewModel.actualizarCampos(nombre, it, hora, minuto) },
                        label = { Text("Dosis (Ej: 1 tableta)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Selector de Hora
                    OutlinedTextField(
                        value = String.format("%02d:%02d", hora, minuto),
                        onValueChange = {},
                        label = { Text("Hora de toma") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { timePickerDialog.show() },
                        leadingIcon = { Icon(Icons.Default.AccessAlarm, null) },
                        enabled = false, // Deshabilitado para escritura, click manejado arriba
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Button(
                        onClick = { viewModel.guardarMedicamento() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Programar Recordatorio")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Horarios Programados", style = MaterialTheme.typography.titleMedium)

            // Lista
            LazyColumn {
                items(lista) { item ->
                    MedicineItem(item, onDelete = { viewModel.eliminarMedicamento(item) })
                }
            }
        }
    }
}

@Composable
fun MedicineItem(item: com.example.medicationtracker.data.local.MedicationEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(item.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${item.dosis} • ${String.format("%02d:%02d", item.hora, item.minuto)}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}