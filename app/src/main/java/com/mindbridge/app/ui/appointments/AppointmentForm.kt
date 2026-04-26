package com.mindbridge.app.ui.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbridge.app.data.model.Appointment
import com.mindbridge.app.data.model.AppointmentStatus
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.theme.*
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
    
    val isFormValid = selectedCaseId.isNotBlank() && titolo.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuovo Appuntamento") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Dettagli Seduta", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            OutlinedTextField(
                value = titolo,
                onValueChange = { titolo = it },
                label = { Text("Tipo di Seduta") },
                placeholder = { Text("es. Colloquio Individuale") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Text("Seleziona Caso / Assistito", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    casi.forEach { careCase ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCaseId = careCase.id }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedCaseId == careCase.id,
                                onClick = { selectedCaseId = careCase.id }
                            )
                            Text(careCase.titolo, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = durata,
                    onValueChange = { if (it.all { char -> char.isDigit() }) durata = it },
                    label = { Text("Durata (min)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                )
                
                Button(
                    onClick = { /* In una versione reale: DatePicker */ },
                    modifier = Modifier.weight(1f).height(56.dp).padding(top = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal50, contentColor = Teal700)
                ) {
                    Icon(Icons.Default.CalendarMonth, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Domani")
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (opzionale)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isFormValid) {
                        val newAppt = Appointment(
                            id = "a${System.currentTimeMillis()}",
                            caseId = selectedCaseId,
                            terapeutaId = therapistId,
                            titolo = titolo,
                            dataOra = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                            durataMinuti = durata.toIntOrNull() ?: 50,
                            note = note,
                            status = AppointmentStatus.CONFERMATO
                        )
                        onSave(newAppt)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Violet500),
                enabled = isFormValid
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(12.dp))
                Text("Conferma e Salva", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(Modifier.height(20.dp))
        }
    }
}
