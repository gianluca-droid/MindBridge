package com.mindbridge.app.ui.appointments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.mindbridge.app.data.model.Appointment
import com.mindbridge.app.data.model.AppointmentStatus
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
    
    // Gestione Data e Ora
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var selectedTime by remember { mutableStateOf(LocalTime.of(10, 0)) }
    
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
    )
    val timeState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute
    )

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

            Text("Caso / Assistito", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            
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
                            RadioButton(selected = selectedCaseId == careCase.id, onClick = { selectedCaseId = careCase.id })
                            Text(careCase.titolo, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Selezione Data e Ora
            Text("Data e Ora", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal50, contentColor = Teal700)
                ) {
                    Icon(Icons.Default.CalendarMonth, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yy")))
                }
                
                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal50, contentColor = Teal700)
                ) {
                    Icon(Icons.Default.AccessTime, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                }
            }

            OutlinedTextField(
                value = durata,
                onValueChange = { if (it.all { char -> char.isDigit() }) durata = it },
                label = { Text("Durata (minuti)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note interne") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isFormValid) {
                        onSave(Appointment(
                            id = "a${System.currentTimeMillis()}",
                            caseId = selectedCaseId,
                            terapeutaId = therapistId,
                            titolo = titolo,
                            dataOra = LocalDateTime.of(selectedDate, selectedTime),
                            durataMinuti = durata.toIntOrNull() ?: 50,
                            note = note,
                            status = AppointmentStatus.CONFERMATO
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Violet500),
                enabled = isFormValid
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(12.dp))
                Text("Salva Appuntamento", fontWeight = FontWeight.Bold)
            }
        }
    }

    // DATE PICKER DIALOG
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let {
                        selectedDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = dateState) }
    }

    // TIME PICKER DIALOG
    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Seleziona Orario", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(20.dp))
                    TimePicker(state = timeState)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showTimePicker = false }) { Text("Annulla") }
                        TextButton(onClick = {
                            selectedTime = LocalTime.of(timeState.hour, timeState.minute)
                            showTimePicker = false
                        }) { Text("OK") }
                    }
                }
            }
        }
    }
}
