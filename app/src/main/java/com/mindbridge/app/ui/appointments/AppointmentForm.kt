package com.mindbridge.app.ui.appointments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.model.Appointment
import com.mindbridge.app.data.model.AppointmentStatus
import com.mindbridge.app.data.repository.MockRepository
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentForm(
    therapistId: String,
    initialCaseId: String? = null,
    onDismiss: () -> Unit,
    onSave: (Appointment) -> Unit
) {
    val casi = remember { MockRepository.getCasesForTherapist(therapistId) }
    
    var selectedCaseId by remember { mutableStateOf(initialCaseId ?: casi.firstOrNull()?.id ?: "") }
    var titolo by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var durata by remember { mutableStateOf("50") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuovo Appuntamento") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val newAppt = Appointment(
                                id = "a${System.currentTimeMillis()}",
                                caseId = selectedCaseId,
                                terapeutaId = therapistId,
                                titolo = titolo.ifBlank { "Seduta" },
                                dataOra = LocalDateTime.now().plusDays(1), // Mock: domani
                                durataMinuti = durata.toIntOrNull() ?: 50,
                                note = note,
                                status = AppointmentStatus.CONFERMATO
                            )
                            onSave(newAppt)
                        }
                    ) {
                        Icon(Icons.Default.Save, null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Dettagli Seduta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            OutlinedTextField(
                value = titolo,
                onValueChange = { titolo = it },
                label = { Text("Titolo/Tipo Seduta") },
                placeholder = { Text("es. Seduta di coppia") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Seleziona Caso", style = MaterialTheme.typography.labelLarge)
            casi.forEach { careCase ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedCaseId == careCase.id,
                        onClick = { selectedCaseId = careCase.id }
                    )
                    Text(careCase.titolo, modifier = Modifier.padding(start = 8.dp))
                }
            }

            OutlinedTextField(
                value = durata,
                onValueChange = { durata = it },
                label = { Text("Durata (minuti)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note interne") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Button(
                onClick = { /* In una versione reale aprirebbe un DatePicker */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Text("Seleziona Data e Ora")
            }
        }
    }
}
