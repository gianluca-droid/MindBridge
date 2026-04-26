package com.mindbridge.app.ui.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mindbridge.app.data.repository.MockRepository
import com.mindbridge.app.ui.cases.AppointmentItem
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    therapistId: String,
    onNewAppointmentClick: () -> Unit
) {
    val allAppointments = remember { MockRepository.getAppointmentsForTherapist(therapistId) }
    val today = LocalDate.now()
    
    val todayAppts = allAppointments.filter { it.dataOra.toLocalDate() == today }
    val futureAppts = allAppointments.filter { it.dataOra.toLocalDate().isAfter(today) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agenda", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewAppointmentClick) {
                Icon(Icons.Default.Add, "Nuovo Appuntamento")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (todayAppts.isNotEmpty()) {
                item { SectionTitle("Oggi") }
                items(todayAppts) { AppointmentCard(it) }
            }

            if (futureAppts.isNotEmpty()) {
                item { SectionTitle("Prossimi") }
                items(futureAppts) { AppointmentCard(it) }
            }
            
            if (allAppointments.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nessun appuntamento in programma", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun AppointmentCard(appt: com.mindbridge.app.data.model.Appointment) {
    val case = remember(appt.caseId) { MockRepository.getCaseById(appt.caseId) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = appt.dataOra.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Text("(${appt.durataMinuti} min)", style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(appt.titolo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                case?.titolo ?: "Caso sconosciuto",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(Modifier.height(12.dp))
            
            Row {
                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Text(appt.status.name, modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
        }
    }
}
